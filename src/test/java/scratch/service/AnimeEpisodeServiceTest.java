package scratch.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.test.ContextTest;

public class AnimeEpisodeServiceTest extends ContextTest{

	@Autowired
	private AnimeEpisodeService service;
	
	@Test
	public void queryTest() {
		service.listScratch(null);
		service.listTodayPass();
	}
	
	

}
