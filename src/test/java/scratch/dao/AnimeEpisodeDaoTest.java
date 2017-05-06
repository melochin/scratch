package scratch.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.service.bilibili.AnimeScratch;
import scratch.test.ContextTest;

public class AnimeEpisodeDaoTest extends ContextTest{

	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	@Autowired
	private AnimeScratch scratch;
	
	@Test
	@Rollback(true)
	@Transactional
	public void save() {
		Anime anime = new Anime(new Long(1));
		AnimeEpisode episode = new AnimeEpisode();
		episode.setAnime(anime);
		episodeDao.save(episode);
	}
	
	@Test
	public void findByUrl() {
		episodeDao.findByUrl("www");
	}
	
	@Test
	public void read() {
		scratch.run();
	}
	
}
