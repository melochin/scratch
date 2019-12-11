package scratch.service.adpater;

import java.util.List;

import org.junit.Test;

import org.springframework.security.web.util.UrlUtils;
import scratch.api.renren.RenrenImpl;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

import static org.junit.Assert.*;

public class RenrenAdapterTest {

	private RenrenAdapter adpater = new RenrenAdapter(new RenrenImpl());

	@Test
	public void testEmptyRead() {
		Anime anime = new Anime();
		List<AnimeEpisode> episodes = adpater.readAnimeEpidsodes(anime, null);
		assertNull(episodes);
	}

	@Test
	public void testRead() {
		Anime anime = new Anime();
		List<AnimeEpisode> episodes = adpater.readAnimeEpidsodes(anime, "Good Doctor");
		assertNotNull(episodes);
		assertTrue(episodes.size() > 0);

		for(AnimeEpisode episode : episodes) {
			assertNotNull(episode.getNumber());
			assertTrue(UrlUtils.isAbsoluteUrl(episode.getUrl()));
		}

	}

}
