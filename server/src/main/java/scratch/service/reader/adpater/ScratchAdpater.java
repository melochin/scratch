package scratch.service.reader.adpater;

import java.io.Serializable;
import java.util.List;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public interface ScratchAdpater extends Serializable {

	/**
	 * @param anime
	 * @return 必须包含 number url
	 *
	 */
	List<AnimeEpisode> readAnimeEpidsodes(Anime anime, String keyword);

	Long getHostId();

}
