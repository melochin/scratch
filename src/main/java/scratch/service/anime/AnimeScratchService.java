package scratch.service.anime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import scratch.api.dilidili.DilidiliImpl;
import scratch.api.fix.FixImpl;
import scratch.api.renren.RenrenImpl;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeAlias;
import scratch.model.AnimeEpisode;
import scratch.service.reader.adpater.Adapter;
import scratch.service.reader.adpater.DilidiliAdapter;
import scratch.service.reader.adpater.FixAdapter;
import scratch.service.reader.adpater.RenrenAdapter;

@Service
public class AnimeScratchService {

	private final static Logger log = Logger.getLogger(AnimeScratchService.class);
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	/**
	 * key: host code, value: anime alias list; 
	 * hsot code = 0, 认为anime没有维护任何host的alias 
	 */
	private Map<Long, List<Anime>> animeAliasMap = new LinkedHashMap<Long, List<Anime>>();
	
	/** key: host code , value: adapater */
	private Map<Long, Adapter> adpaterMap = new HashMap<Long, Adapter>();
	
	/** init adapterMap */
	{
		this.adpaterMap.put(new Long(1), new DilidiliAdapter(new DilidiliImpl()));
		this.adpaterMap.put(new Long(2), new FixAdapter(new FixImpl()));
		this.adpaterMap.put(new Long(3), new RenrenAdapter(new RenrenImpl()));
	}
	
	/** init AnimeAliasMap */
	private synchronized void init() {
		// 获取未完结的番剧，且含有别名
		List<Anime> animes = animeDao.findWithAlias();
		for(Anime anime : animes) {
			// 没有维护别名的，hostId全部默认为0，别名即为Anime name
			if(CollectionUtils.isEmpty(anime.getAliass())) {
				fillAnimeAliasMap(new Long(0), anime.getName(), anime);
			} 
			// 有别名的，存放对应hostId的AnimeList中
			else {
				
				for(AnimeAlias alias : anime.getAliass()) {
					fillAnimeAliasMap(alias.getHostId(), alias.getAlias(), anime);
				}
			}
		}
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
		
		List<Anime> animes = animeAliasMap.get(hostId);
		if(animes == null) {
			animes = new ArrayList<Anime>();
		}
		
		return animes;
	}
	
	
	@Scheduled(fixedRate=60*60*1000)
	public void run() {
		// 初始化
		init();
		
		List<AnimeEpisode> list = new ArrayList<AnimeEpisode>();
		
		if(log.isInfoEnabled()) {
			log.info("start runing scratch service");
		}
		
		// 遍历所有适配器，执行数据抓取任务
		for(Entry<Long, Adapter> entry : adpaterMap.entrySet()) {
			
			// 获取HostId及对应的适配器
			Long hostId = entry.getKey();
			Adapter adapter = entry.getValue();
			
			// 加载对应的ANIME数据
			List<Anime> animes = getAnimeMap(hostId);
			
			// 使用适配器抓取数据
			for(Anime anime : animes) {
				List<AnimeEpisode> episodes = adapter.readAnimeEpidsode(anime);
				for(AnimeEpisode episode : episodes) {
					episode.setHostId(hostId);
				}
				list.addAll(episodes);
			}
		}
		
		if(log.isInfoEnabled()) {
			log.info("end scratch service \n" + 
					" get " + list.size() + " episode");
		}
		
		//保存数据
		saveEpisode(list);
	}
	
	
	//保存数据
	private void saveEpisode(List<AnimeEpisode> animeEpisodes) {
		for(AnimeEpisode episode : animeEpisodes) {
			//判断数据中是否已经存在,存在则跳过
			if(episodeDao.findByUrl(episode.getUrl()) != null) continue;
			
			//根据别名查找番剧对象，为了将番剧与具体集建立关系
			Anime anime = animeDao.findByAlias(episode.getAnime().getName(), false);
			if(anime == null) continue;
			episode.setAnime(anime);
			episode.setScratchTime(new Date());
			//保存数据
			episodeDao.save(episode);
		}
	}
	
}
