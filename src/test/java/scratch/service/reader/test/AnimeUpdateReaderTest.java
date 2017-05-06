package scratch.service.reader.test;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.AnimeEpisode;
import scratch.service.bilibili.AnimeScratch;
import scratch.service.reader.AnimeUpdateReader;
import scratch.test.ContextTest;

public class AnimeUpdateReaderTest extends ContextTest{
	
	@Autowired
	private AnimeUpdateReader reader;
	
	@Autowired
	private AnimeScratch scratch;
	
	@Test
	public void readTest() {
		List<List<AnimeEpisode>> list = reader.read();
		if(list.size() > 0) {
			for(List<AnimeEpisode> animeEpisodes : list) {
				System.out.println(animeEpisodes);
			}
		}
	}
	
	@Test
	public void scratchTest() {
		scratch.run();
	}
	
}
