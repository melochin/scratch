package scratch.service.reader.adpater;

import java.util.List;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public interface ScratchAdpater {

	/**
	 * @param anime
	 * @return 必须包含 number url
	 *
	 */
	List<AnimeEpisode> readAnimeEpidsodes(Anime anime, String keyword);

}
