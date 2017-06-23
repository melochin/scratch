package scratch.service.reader.adpater;

import java.util.List;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;

public interface Adapter {
	
	List<AnimeEpisode> readAnimeEpidsode(Anime anime);

}
