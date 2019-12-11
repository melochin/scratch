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
		String url = renren.search("泰坦")
				.get(0).getDownloadListUrl();

		System.out.println(renren.getEpisodeList(url));
		Assert.assertTrue(
				renren.getEpisodeList(url).size() > 0);
	}

	@Test
	public void isValidate() {
		boolean validate = renren.isValidate("https://pan.baidu.com/s/1kW19hyf");
		assertFalse(validate);
	}

}