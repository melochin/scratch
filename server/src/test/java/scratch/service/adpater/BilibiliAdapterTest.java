package scratch.service.reader.adpater;

import org.junit.Test;
import org.springframework.security.web.util.UrlUtils;
import scratch.api.bilibili.BilibiliImpl;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

import java.util.List;

import static org.junit.Assert.*;

public class BilibiliAdapterTest {

	private ScratchAdpater adapter = new BilibiliAdapter(new BilibiliImpl());

	@Test
	public void read() throws Exception {
		Anime anime = new Anime();
		List<AnimeEpisode> episodes = adapter.readAnimeEpidsodes(anime,"岚");
		assertNotNull(episodes);
		assertTrue(episodes.size() > 0);

		for(AnimeEpisode episode : episodes) {
			assertNotNull(episode.getNumber());
			assertTrue(UrlUtils.isAbsoluteUrl(episode.getUrl()));
		}
	}

	@Test
	public void readFocus() {
		Anime anime = new Anime();
		List<AnimeEpisode> episdoes =  adapter.readAnimeEpidsodes(anime, "2");
		assertNotNull(episdoes);
		assertTrue(episdoes.size() > 0);
	}

	@Test
	public void readEmpty() {
		Anime anime = new Anime();
		assertNull(adapter.readAnimeEpidsodes(anime, null));
	}

}