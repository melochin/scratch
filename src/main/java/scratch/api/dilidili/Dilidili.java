package scratch.api.dilidili;

import java.util.List;

import scratch.model.AnimeEpisode;

public interface Dilidili {

	List<AnimeEpisode> getEpisodeList(String alias);
	
}
