package scratch.service.reader.renren;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import scratch.api.renren.RenrenImpl;
import scratch.api.renren.Video;
import scratch.api.renren.VideoEpisode;


public class RenrenImplTest{

	private RenrenImpl renren = new RenrenImpl();
	
	@Test
	public void testSearch() {
		List<Video> videos = renren.search("小巨人");
		assertNotNull("List<Video> is null", videos);
		
		for(Video v : videos) {
			assertNotNull("title is null", v.getTitle());
			assertNotNull("publishAt is null", v.getPublishAt());
			assertNotNull("resourceUrl is null",v.getResourceUrl());
		}
		
		System.out.println(videos);
	}
	
	@Test
	public void testGetEpisodeList() {
		List<VideoEpisode> videoEpisodes = renren.getEpisodeList("http://xiazai002.com/S00000");
		assertNotNull(videoEpisodes);
		System.out.println(videoEpisodes);
	}

}
