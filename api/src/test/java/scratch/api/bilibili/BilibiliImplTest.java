package scratch.api.bilibili;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scratch.api.bilibili.Video;

public class BilibiliImplTest {

	private Bilibili bilibili = new BilibiliImpl();

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

}
