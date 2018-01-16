package scratch.api.renren;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import scratch.api.renren.RenrenImpl;
import scratch.api.renren.Video;
import scratch.api.renren.VideoEpisode;


public class RenrenImplTest{

	private RenrenImpl renren = new RenrenImpl();
	
	@Test
	public void testSearch() {
		List<Video> videos = renren.search("致命之吻");
		assertNotNull("List<Video> is null", videos);

		for(Video v : videos) {
			assertNotNull("title is null", v.getTitle());
			assertNotNull("publishAt is null", v.getPublishAt());
			assertNotNull("resourceUrl is null",v.getResourceUrl());
			assertNotNull("downloadListUrl is null", v.getDownloadListUrl());
			List<VideoEpisode> videoEpisodes = renren.getEpisodeList(v.getDownloadListUrl());
			assertNotNull(videoEpisodes);
		}

	}

}
