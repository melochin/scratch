package scratch.api.renren;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RenrenImplTest {

	private Renren renren = new RenrenImpl();

	@Test
	public void search() throws Exception {
		Assert.assertTrue(
				renren.search("致命之吻").size() > 0);
	}

	@Test
	public void getEpisodeList() throws Exception {
		String url = renren.search("致命之吻")
				.get(0).getDownloadListUrl();
		Assert.assertTrue(
				renren.getEpisodeList(url).size() > 0);
	}

}