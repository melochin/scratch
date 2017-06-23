package scratch.service.reader.adpater;

import java.util.List;

import scratch.api.fix.Fix;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;

public class FixAdapter implements Adapter{

	private Fix fix;
	
	public FixAdapter(Fix fix) {
		this.fix = fix;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsode(Anime anime) {
		
		List<AnimeEpisode> episodes = fix.getDownloadList(anime.getAlias());
		
		for(AnimeEpisode episode : episodes) {
			episode.setAnime(anime);
		}
		
		return episodes;
	}


}
