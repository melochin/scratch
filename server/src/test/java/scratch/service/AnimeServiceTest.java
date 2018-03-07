package scratch.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.redis.core.RedisTemplate;
import scratch.dao.inter.IAnimeDao;
import scratch.model.entity.Anime;

public class AnimeServiceTest {

	private IAnimeDao animeDao;

	private RedisTemplate redisTemplate;
	
	private AnimeService service;
	
	private List<Anime> animes;
	
	{
		animes = new ArrayList<Anime>();
		Anime anime1 = new Anime("Test");
		anime1.setType("1");
		Anime anime2 = new Anime("Test2");
		anime2.setType("2");
		animes.add(anime1);
		animes.add(anime2);
	}
	
	@Before
	public void setUp() {
		animeDao = mock(IAnimeDao.class);
		redisTemplate = mock(RedisTemplate.class);
		service = new AnimeService(animeDao, redisTemplate);
	} 
	
	@Test
	public void findTest() {
		when(animeDao.listIf("1", null)).thenReturn(animes);
		assertTrue(service.pageByType("1", 0).getPage().getTotalPage() == 2);
		verify(animeDao).listIf("1", null);
	}
	
	@Test
	public void findMostFcousGroupByType() {
		when(animeDao.listMostFocused()).thenReturn(animes);
		Map<String, List<Anime>> map = service.listMostFcousedGroupByType();
		assertTrue(map.size() == 2);
		verify(animeDao).listMostFocused();
	}
	
	
}
