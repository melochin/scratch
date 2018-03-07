package scratch.service.reader.adpater;

import java.util.List;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public interface Adapter {
	
	List<AnimeEpisode> readAnimeEpidsode(Anime anime);

}
