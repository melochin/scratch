package scratch.api.bilibili;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import scratch.model.Video;

public class BilibiliImplTest {

	private Bilibili bilibili = new BilibiliImpl();

	@BeforeClass
	public static void setLogger() {
		System.setProperty("log4j.configuration","log4j.properties");
	}

	@Test
	public void test() {
		List<Video> videos = bilibili.getVideos(VideoType.MUSIC_SYNTHESIZED, 1);
		System.out.println(videos);
		Assert.assertTrue(videos.size() > 0);
	}
	
	@Test
	public void searchTest() {
		List<Video> videos = bilibili.search("交给岚");
		Assert.assertTrue(videos.size() > 0);
	}

}
