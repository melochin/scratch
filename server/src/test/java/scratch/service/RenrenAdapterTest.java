package scratch.service;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scratch.api.renren.RenrenImpl;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.service.reader.adpater.RenrenAdapter;

public class RenrenAdapterTest {

	private RenrenAdapter adpater = new RenrenAdapter(new RenrenImpl());
	
	
	@Test
	public void testReadAnimeEpidsode() {
		Anime anime = new Anime();
		anime.setName("少年谢尔顿");
		List<AnimeEpisode> episodes = adpater.readAnimeEpidsode(anime);
		Assert.assertNotNull(episodes);
		System.out.println(episodes);
	}

}
