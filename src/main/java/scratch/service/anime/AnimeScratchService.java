package scratch.service.anime;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.service.reader.anime.DiliReader;
import scratch.service.reader.anime.FixReader;

@Service
public class AnimeScratchService {

	private final static Logger log = Logger.getLogger(AnimeScratchService.class);
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	private Map<String, Anime> animeNameMap =  new LinkedHashMap<String, Anime>();

	private Map<String, Anime> animeAliasMap = new LinkedHashMap<String, Anime>();
	
	public synchronized void init() {
		//寻找未完结的番剧
		List<Anime> animes = animeDao.findByFinished(false);
		for(Anime anime : animes) {
			animeNameMap.put(anime.getName(), anime);
			animeAliasMap.put(anime.getAlias(), anime);
		}
	}
	
	public synchronized Anime getAnime(String name) {
		//先从名字里找
		if(animeNameMap.containsKey(name)) {
			return animeNameMap.get(name);
		} 
		//再从别名里找
		if(animeAliasMap.containsKey(name)) {
			return animeAliasMap.get(name);
		}
		return null;
	}
	
	
	//抓取Anime信息
	/*@Scheduled(cron="0 07 17 * * ?")*/
	//@Scheduled(fixedRate=60*60*1000)
	public void diliRun() {
		if(log.isInfoEnabled()) {
			log.info("start runing dili scratch service");
		}
		
		List<AnimeEpisode> animeEpisodes = null;
		
		try{
			animeEpisodes = new DiliReader().read();
			saveEpisode(animeEpisodes);
		} catch (Exception e) {
			if(log.isInfoEnabled()) {
				log.error("dili scratch service abort: " + e.getMessage());
			}
		} finally {
			if(log.isInfoEnabled()) {
				log.info("end dili scratch service \n" + 
						" get " + animeEpisodes.size() + " episode");
			}
		}
	}
	
	/**
	 * FIX抓取服务，延迟半小时执行，与D站服务保持在半小时时间差
	 * 修改：读取具体的episode之前，判断下anime是否存在，判断方法由service提供
	 */
	//@Scheduled(fixedRate=60*60*1000, initialDelay=30*60*1000)
	public void fixRun() {
		
		if(log.isInfoEnabled()) {
			log.info("start runing fix scratch service");
		}
		
		init();
		
		List<AnimeEpisode> episodeList = new ArrayList<AnimeEpisode>();
		
		try {
			for(int i=1; i<=10; i++) {
				List<AnimeEpisode> episodes = new FixReader(i, this).read();
				episodeList.addAll(episodes);
			}
			saveEpisode(episodeList);
		} catch (Exception e) {
			
			if(log.isInfoEnabled()) {
				log.error("fix scratch service abort: " + e.getStackTrace());
			}
			
		} finally {
			
			if(log.isInfoEnabled()) {
				log.info("end fix scratch service: " + 
						"get " + episodeList.size() + " episode");
			}
		}
		
	}
	
	public void saveEpisode(List<AnimeEpisode> animeEpisodes) {
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
