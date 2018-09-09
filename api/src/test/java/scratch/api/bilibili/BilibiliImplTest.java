package scratch.api.bilibili;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scratch.api.bilibili.Video;
import scratch.support.web.HttpConnection;
import scratch.support.web.net.Flow;

public class BilibiliImplTest {

	private Bilibili bilibili = new BilibiliImpl();


	@Test
	public void focus() throws Exception {

		List<Video> videos = bilibili.focus(new Long(14016058));
		System.out.println(videos);
		Assert.assertTrue(videos.size() > 0);

	}

	@Test
	public void test() {
		List<Video> videos = bilibili.getVideos(VideoType.MUSIC_SYNTHESIZED, 1);
		Assert.assertTrue(videos.size() > 0);
	}

	@Test
	public void searchTest() {
		List<Video> videos = bilibili.search("交给岚");
		System.out.println(videos.size());
		Assert.assertTrue(videos.size() > 0);
	}

	@Test
	public void isValidateTest() throws MalformedURLException {
		boolean validate = bilibili.isValidate("https://www.bilibili.com/video/av26954059");
		Assert.assertFalse(validate);
	}

}
