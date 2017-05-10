package scratch.service.anime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	//抓取Anime信息
	/*@Scheduled(cron="0 07 17 * * ?")*/
	@Scheduled(fixedRate=30*60*1000)
	public void diliRun() {
		if(log.isInfoEnabled()) {
			log.info("start runing dili scratch service");
		}
		
		List<AnimeEpisode> animeEpisodes = new DiliReader().read();
		saveEpisode(animeEpisodes);
		
		if(log.isInfoEnabled()) {
			log.info("end dili scratch service");
		}
	}
	
	@Scheduled(fixedRate=30*60*1000)
	public void fixRun() {
		if(log.isInfoEnabled()) {
			log.info("start runing fix scratch service");
		}
		
		List<AnimeEpisode> episodeList = new ArrayList<AnimeEpisode>();
		for(int i=1; i<=10; i++) {
			List<AnimeEpisode> episodes = new FixReader(i).read();
			episodeList.addAll(episodes);
		}
		saveEpisode(episodeList);
		
		if(log.isInfoEnabled()) {
			log.info("end fix scratch service");
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
