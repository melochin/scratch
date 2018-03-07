package scratch.service.anime;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import scratch.api.renren.RenrenImpl;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IScratchRecord;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.ScratchRecord;
import scratch.service.reader.adpater.Adapter;
import scratch.service.reader.adpater.RenrenAdapter;

@Service
public class AnimeScratchService {

	private final static Logger log = Logger.getLogger(AnimeScratchService.class);
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IScratchRecord recordDao;
	
	@Autowired
	private AnimeMessageService messsageService;

	private static Boolean isScratchRun = false;

	private static Boolean isFirst = true;

	/**
	 * key: host code, value: anime alias list; 
	 * hsot code = 0, 认为anime没有维护任何host的alias 
	 */
	private Map<Long, List<Anime>> animeAliasMap = new LinkedHashMap<Long, List<Anime>>();
	
	/** key: host code , value: adapater */
	private Map<Long, Adapter> adpaterMap = new HashMap<Long, Adapter>();
	
	/** init adapterMap */
	{
	//	this.adpaterMap.put(new Long(1), new DilidiliAdapter(new DilidiliImpl()));
	//	this.adpaterMap.put(new Long(2), new FixAdapter(new FixImpl()));
		this.adpaterMap.put(new Long(3), new RenrenAdapter(new RenrenImpl()));
	//	this.adpaterMap.put(new Long(4), new BilibiliAdapter(new BilibiliImpl()));
	}

	@Scheduled(fixedRate=2*60*1000)
	public void run() {

		// 运行状态判断，防止多个任务在执行
		if(!updateRun(true)) {
			if(log.isInfoEnabled()) {
				log.debug("scratch service is running, waiting for next time");
			}
			return;
		}

		// 初始化
		initAnimeAliasMap();
		LogMessage.clear();

		// 准备多线程
		ExecutorService exec = Executors.newCachedThreadPool();
		CountDownLatch countDownLatch = new CountDownLatch(adpaterMap.size());
		ConcurrentMap<String, Integer> resultMap = new ConcurrentHashMap<String, Integer>();

		if(log.isInfoEnabled()) {
			log.info("start runing scratch service");
		}

		// 遍历所有适配器，执行数据抓取线程任务
		adpaterMap.entrySet().forEach(entry -> {
			// 获取HostId及对应的适配器
			Long hostId = entry.getKey();
			Adapter adapter = entry.getValue();
			// 加载对应的ANIME数据
			List<Anime> animes = getAnimeMap(hostId);
			// 网络访问任务可能超时，启用线程任务
			exec.execute(() -> {
				int episodeCount = readAndPushAnimeEpidsode(adapter, animes, hostId);
				resultMap.put(adapter.getClass().getName(), episodeCount);
				countDownLatch.countDown();
			});

		});

		// 结束线程，等待所有scratch任务完成
		// 1.输出运行结果 2.重置运行状态
		exec.execute(() -> {
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}

			int count = resultMap.entrySet().stream()
					.mapToInt(e -> e.getValue()).sum();
			if(log.isInfoEnabled()) {
				log.info("end scratch service : get " + count + " episode \n"
						+ resultMap);
			}

			LogMessage.add("运行完毕：共抓取" + count + "条数据");
			//重置运行状态
			updateRun(false);
		});

		exec.shutdown();
	}

	// 运行状态
	public boolean isRun() {
		return isScratchRun;
	}

	public synchronized List<String> getMessages() {
		List<String> list = new ArrayList<>();
		list.addAll(LogMessage.get());
		return list;
	}

	// 运行记录
	public Map<String, Integer> getRecordMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		List<ScratchRecord> records = recordDao.list();
		for(ScratchRecord record : records) {
			long plus = (record.getEndTime().getTime() - record.getStartTime().getTime()) / 1000;
			plus = plus == 0 ? 1 : plus;
			long unit = record.getCount() / plus;
			String endTime = DateFormatUtils.format(record.getEndTime(), DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern());
			map.put(endTime, new Integer((int) unit));
		}
		return map;
	}


	/** init AnimeAliasMap */
	private synchronized void initAnimeAliasMap() {
		animeAliasMap = new LinkedHashMap<Long, List<Anime>>();
		// 获取未完结的番剧，且含有别名
		List<Anime> animes = animeDao.listWithAlias();
		animes.forEach(anime -> {
			// 没有维护别名的，hostId全部默认为0，别名即为Anime name
			if(CollectionUtils.isEmpty(anime.getAliass())) {
				fillAnimeAliasMap(new Long(0), anime.getName(), anime);
			}
			// 有别名的，存放对应hostId的AnimeList中
			else {
				anime.getAliass().forEach(alias -> {
					fillAnimeAliasMap(alias.getHostId(), alias.getAlias(), anime);
				});
			}
		});
	}
	
	/** 填充AnimeAliasMap，为每个站点生成对应的Anime List  */
	private void fillAnimeAliasMap(Long hostId, String alias, Anime anime) {
		if(!animeAliasMap.containsKey(hostId)) {
			animeAliasMap.put(hostId, new ArrayList<Anime>());
		}
		anime.setAlias(alias);
		animeAliasMap.get(hostId).add(anime);
	}
	
	private synchronized List<Anime> getAnimeMap(Long hostId) {
		return Optional.ofNullable(animeAliasMap.get(hostId)).orElse(new ArrayList<Anime>());
	}

	/**
	 *
	 * @param adapter 适配器
	 * @param animes 对应站点需要抓取的anime
	 * @param hostId 站点ID
	 * @return
	 */
	private int readAndPushAnimeEpidsode(Adapter adapter, List<Anime> animes, Long hostId) {
		int count = animes.stream().mapToInt(anime -> {
			int size = 0;
			try{
				// 网络访问 抓取数据
				List<AnimeEpisode> episodes = adapter.readAnimeEpidsode(anime);
				LogMessage.add("[完成] HostId:" + hostId + " Anime:" + anime.getName() +
						" 抓取" + episodes.size() +"条数据");
				// 设置默认值
				if(episodes == null) return size;
				episodes.forEach(e -> {
					e.setHostId(hostId);
					e.setAnime(anime);
					e.setScratchTime(new Date());
				});
				// 发送给消息队列
				messsageService.push(episodes);
				size = episodes.size();
			} catch (Exception e) {
				LogMessage.add("[错误]" + e.getMessage());
				log.error(e.getMessage());
			}
			return size;
		}).sum();
		return count;
	}

	/**
	 * 更新运行状态
	 * @param exceptedStatus
	 * @return
	 */
	private boolean updateRun(boolean exceptedStatus) {
		synchronized (isScratchRun) {
			if(isScratchRun == exceptedStatus) return false;
			isScratchRun = exceptedStatus;
		}
		return true;
	}


}
