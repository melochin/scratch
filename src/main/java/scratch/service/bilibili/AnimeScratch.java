package scratch.service.bilibili;

import java.util.Calendar;
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
import scratch.service.reader.AnimeUpdateReader;

@Service
public class AnimeScratch {

	private final static Logger log = Logger.getLogger(AnimeScratch.class);
	
	@Autowired
	private IAnimeDao animeDao;
	
	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	@Autowired
	private AnimeUpdateReader reader;
	
	//抓取Anime信息
	/*@Scheduled(cron="0 07 17 * * ?")*/
/*	@Scheduled(fixedRate=30000)
*/	public void run() {
		List<List<AnimeEpisode>> animeEpisodesList = reader.read();
		if(animeEpisodesList.size() < Calendar.DAY_OF_WEEK) {
			log.error("番剧更新信息有误：没有一周的信息");
		}
		
		for(int i=0; i<Calendar.DAY_OF_WEEK; i++) {
			List<AnimeEpisode> animeEpisodes = animeEpisodesList.get(i);
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
}
