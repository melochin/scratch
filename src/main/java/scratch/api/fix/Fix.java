package scratch.api.fix;

import java.util.List;

import scratch.model.entity.AnimeEpisode;

public interface Fix {

	List<AnimeEpisode> getDownloadList(String videoName);
	
}
