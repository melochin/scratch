package scratch.api.dilidili;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scratch.api.dilidili.Dilidili;
import scratch.api.dilidili.DilidiliImpl;
import scratch.model.AnimeEpisode;

public class DilidiliImplTest {

	private Dilidili dilidili = new DilidiliImpl();
	
	@Test
	public void test() {
		List<AnimeEpisode> list = dilidili.getEpisodeList("bzjdmsyjjjd");
		Assert.assertTrue(list.size() > 0);
	}

}
