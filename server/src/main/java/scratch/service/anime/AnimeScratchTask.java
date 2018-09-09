package scratch.service.anime;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scratch.api.bilibili.BilibiliImpl;
import scratch.api.renren.RenrenImpl;
import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeScratchDao;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;
import scratch.service.reader.adpater.BilibiliAdapter;
import scratch.service.reader.adpater.RenrenAdapter;
import scratch.service.reader.adpater.ScratchAdpater;

import java.util.*;
import java.util.concurrent.*;

import java.util.stream.Collectors;

@Service
public class AnimeScratchTask {

	private final static Logger log = Logger.getLogger(AnimeScratchTask.class);

	private IAnimeDao animeDao;

	private IAnimeAliasDao aliasDao;

	private IAnimeEpisodeScratchDao episodeScratchDao;

	@Autowired
	public AnimeScratchTask(IAnimeDao animeDao,
							IAnimeAliasDao aliasDao,
							IAnimeEpisodeScratchDao episodeScratchDao) {
		this.animeDao = animeDao;
		this.aliasDao = aliasDao;
		this.episodeScratchDao = episodeScratchDao;
	}

	private static Boolean isScratchRun = false;

	private static ExecutorService exec = null;

	/**
	 * 域名编码与Anime列表的对应关系
	 *
	 * key	: Long hostCode 域名编码
	 * value: Map<String, Anime>
	 *     		key: Anime
	 *     		value: List<String> 别名列表
	 * 当hsotCode = 0, 认为anime不对应任何域
	 */
	private Map<Long, Map<Anime, List<String>>> animeAliasMap =  new LinkedHashMap<>();

	/**
	 * 域名编码与对应抓取服务的关系
	 */
	private Map<Long, ScratchAdpater> adpaterMap = new HashMap<>();

	{
		/*this.adpaterMap.put(new Long(1), new DilidiliAdapter(new DilidiliImpl()));
		this.adpaterMap.put(new Long(2), new FixAdapter(new FixImpl()));*/
		this.adpaterMap.put(Long.valueOf(3), new RenrenAdapter(new RenrenImpl()));
		this.adpaterMap.put(Long.valueOf(4), new BilibiliAdapter(new BilibiliImpl()));
	}

	@Transactional
	public void run() {
		// 运行状态判断，防止多个任务在执行
		if(!updateRun(true)) {
			log.info("服务已经在运行，请稍后再试");
			return;
		}
		log.info("服务参数初始化");
		// 参数初始化
		initAnimeAliasMap();
		BlockingQueue<AnimeEpisode> scratchEpisodeList = new LinkedBlockingQueue<>();
		// 准备多线程 (线程数 =  anime的总和，每个anime的抓取任务一个线程)
		exec = Executors.newCachedThreadPool();
		CountDownLatch countDownLatch = new CountDownLatch(adpaterMap.size());
		log.info("初始化完毕，服务开始启动");
		// 遍历所有适配器，执行数据抓取线程任务
		adpaterMap.forEach((hostId, scratchAdpater) -> {

			Map<Anime, List<String>> animes = animeAliasMap.get(hostId);
			// 启用线程任务（原因：1.并发执行提高速度 2.网络访问可能超时，造成阻塞）
			exec.execute(() -> {
				// 抓取数据并将获得的数据发送到消息队列
				List<AnimeEpisode> scratchEpisodes = scratchEpisodes(scratchAdpater, animes, hostId);
				scratchEpisodeList.addAll(scratchEpisodes);
				countDownLatch.countDown();
			});

		});

		// 结束线程，等待所有scratch任务完成
		// 1.输出运行结果 2.重置运行状态
		exec.execute(() -> {
			try {
				countDownLatch.await();
				// 准备保存操作
				List<AnimeEpisode> result = new ArrayList<>();
				scratchEpisodeList.drainTo(result);
				int saveCount = saveScrtachs(result);
				log.info("任务完成, 有效保存数据:" + saveCount + "条");
			} catch (InterruptedException e) {
				log.error(e, e);
			} finally {
				updateRun(false);
			}
		});

		exec.shutdown();
	}

	/**
	 * 初始化 animeAliasMap
	 * key : hostId
	 * value : List<Anime>	说明：Anime是指对应的key:hostId存在别名的，且未完结的Anime
	 */
	private synchronized void initAnimeAliasMap() {
		// 获取未完结的番剧，且含有别名
		List<Anime> animes = animeDao.listIf(null, false).stream()
				.map(anime -> {
					anime.setAliass(aliasDao.list(anime.getId()));
					return anime;
				})
				.filter(anime -> anime.getAliass() != null && anime.getAliass().size() > 0)
				.collect(Collectors.toList());

		animes.forEach(anime -> {
			anime.getAliass().forEach(alias -> {
				Long hostId = alias.getHostId();
				if(!animeAliasMap.containsKey(hostId)) {
					animeAliasMap.put(hostId, new HashMap<Anime, List<String>>());
				}
				animeAliasMap.get(hostId).put(anime, alias.getNames());
			});
		});
	}

	/**
	 * 1.网络访问抓取数据
	 * 2.设置抓取数据的默认值
	 * 3.发送数据到消息队列
	 * 4.统计抓取总数
	 * @param scratchAdpater 适配器
	 * @param animes 对应站点需要抓取的anime
	 * @param hostId 站点ID
	 * @return 抓取AnimeEpisode列表
	 */
	private List<AnimeEpisode> scratchEpisodes(final ScratchAdpater scratchAdpater,
											   Map<Anime, List<String>> animes, final Long hostId) {

		List<AnimeEpisode> scratchEpisodes = animes.keySet().stream()
				.map(anime -> doScratchEpisodes(scratchAdpater, anime, animes.get(anime), hostId))
				.filter(episodes -> episodes != null)
				.filter(episodes -> episodes.size() > 0)
				.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

		return scratchEpisodes;
	}

	/**
	 * 主要功能：<br/>
	 * 使用scratchAdpater抓取指定anime的episode数据<br/>
	 * <br/>
	 * 细节说明：<br/>
	 * 1.抓取操作有一定抛出异常几率，捕获记录日志<br/>
	 * 2.对抓取后的数据,进行默认值设置
	 *
	 * @param scratchAdpater
	 * @param anime
	 * @param hostId
	 * @return 抓取的animeEpisode列表
	 */
	private @Nullable List<AnimeEpisode> doScratchEpisodes(ScratchAdpater scratchAdpater,
														   Anime anime, List<String> keywords, Long hostId) {
		List<AnimeEpisode> episodes = null;
		try {
			episodes = keywords.stream()
					.map(keyword -> scratchAdpater.readAnimeEpidsodes(anime, keyword))
					.filter(results -> results != null)
					.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		} catch (Exception e) {
			log.error(e, e);
		}
		if(episodes == null) return null;
		for(AnimeEpisode ep : episodes) {
			ep.setHostId(hostId);
			ep.setAnime(anime);
			ep.setScratchTime(new Date());
		}
		log.info("HostId:" + hostId +
				" Anime:" + anime.getName() +
				" 获取" + episodes.size() + "条数据");
		return episodes;
	}

	private int saveScrtachs(List<AnimeEpisode> episodes) {
		List<AnimeEpisodeScratch> animeEpisodeScratches = episodes.stream()
				.filter(ep -> !episodeScratchDao.isExistUrl(ep.getUrl()) )
				.map(AnimeEpisodeScratch::new)
				.collect(Collectors.toList());

		animeEpisodeScratches.forEach(ep -> episodeScratchDao.save(ep));
		return animeEpisodeScratches.size();
	}

	/**
	 * 更新运行状态
	 * @param exceptedStatus 期望运行状态
	 * @return 更新是否成功
	 */
	private boolean updateRun(boolean exceptedStatus) {
		synchronized (isScratchRun) {
			if(isScratchRun == exceptedStatus) return false;
			isScratchRun = exceptedStatus;
		}
		return true;
	}

	/**
	 * @return 服务运行状态
	 */
	public synchronized boolean isRun() {
		// 状态位 == true
		// 线程非空 且 在运行
		return isScratchRun || (exec != null && !exec.isTerminated());
	}

}
