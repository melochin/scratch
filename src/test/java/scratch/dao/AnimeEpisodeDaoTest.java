package scratch.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.test.ContextTest;

public class AnimeEpisodeDaoTest extends ContextTest{

	@Autowired
	private IAnimeEpisodeDao episodeDao;
	
	private AnimeEpisode animeEpisode;
	
	{
		Anime anime = new Anime(new Long(1));
		animeEpisode= new AnimeEpisode();
		animeEpisode.setAnime(anime);
		animeEpisode.setHostId(new Long(1));
		animeEpisode.setUrl("www.google.com");
	}
	
	@Rollback
	@Transactional
	@Test
	public void saveTest() {
		assertTrue(episodeDao.save(animeEpisode) == 1);
	}
	
	@Rollback
	@Transactional
	@Test
	public void modifyTest() {
		assertTrue(episodeDao.save(animeEpisode) == 1);
		
		AnimeEpisode updateEpisode = episodeDao.findByUrl(animeEpisode.getUrl());
		updateEpisode.setUrl("www.baidu.com");
		assertTrue(episodeDao.modify(updateEpisode) == 1);
	}
	
	@Rollback
	@Transactional
	@Test
	public void deleteTest(){
		String url = animeEpisode.getUrl();
		assertTrue(episodeDao.save(animeEpisode) == 1);
		
		Long id = episodeDao.findByUrl(url).getId();
		assertTrue(episodeDao.delete(id) == 1);
	}
	
	@Rollback
	@Transactional
	@Test
	public void queryTest() {
		assertTrue(episodeDao.save(animeEpisode) == 1);
		Long id = episodeDao.findByUrl(animeEpisode.getUrl()).getId();
		AnimeEpisode episode = episodeDao.getById(id);
		assertTrue(episode != null);
	}
	
	@Test
	public void findByUrl() {
		episodeDao.findByUrl("www");
	}
	
	@Test
	public void listByAnimeId() {
		episodeDao.listByAnimeId(new Long(13));
	}
	
}
