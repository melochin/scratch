package scratch.api.fix;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scratch.api.fix.Fix;
import scratch.api.fix.FixImpl;
import scratch.model.AnimeEpisode;

public class FixImplTest {

	private Fix fix = new FixImpl();
	
	
	@Test
	public void test() {
		List<AnimeEpisode> list = fix.getDownloadList("硅谷");
		Assert.assertTrue(list.size() > 0);
	}

}
