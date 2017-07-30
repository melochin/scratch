package scratch.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.Anime;
import scratch.test.ContextTest;

public class AnimeServiceTest extends ContextTest{

	@Autowired
	private AnimeService service;
	
	@Test
	public void test() {
		String type = "22";
		int page = 1;
		
		List<Anime> animes = service.find(type, page);
		Assert.assertTrue(animes != null);
		service.find(type, -1);
	}

}
