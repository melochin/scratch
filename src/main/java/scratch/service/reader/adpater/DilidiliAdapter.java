package scratch.service.reader.adpater;

import java.util.List;

import scratch.api.dilidili.Dilidili;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public class DilidiliAdapter implements Adapter{

	private Dilidili dilidili;
	
	public DilidiliAdapter(Dilidili dilidili) {
		this.dilidili = dilidili;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsode(Anime anime) {
		List<AnimeEpisode> animeEpisodes = dilidili.getEpisodeList(anime.getAlias());
		
		for(AnimeEpisode episode : animeEpisodes) {
			episode.setAnime(anime);
		}
		
		return animeEpisodes;
	}

}
