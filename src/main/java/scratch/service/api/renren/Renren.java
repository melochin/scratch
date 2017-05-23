package scratch.service.api.renren;

import java.util.List;

public interface Renren {
	
	List<Video> search(String keyword);

	List<VideoEpisode> getEpisodeList(String downloadUrl);
	
}
