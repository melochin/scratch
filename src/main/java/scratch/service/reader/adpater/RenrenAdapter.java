package scratch.service.reader.adpater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import scratch.api.renren.Renren;
import scratch.api.renren.Video;
import scratch.api.renren.VideoEpisode;
import scratch.model.Anime;
import scratch.model.AnimeEpisode;

public class RenrenAdapter implements Adapter{

	private Renren renren;
	
	public RenrenAdapter(Renren renren) {
		this.renren = renren;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsode(Anime anime) {
		
		String name = anime.getName();
		List<Video> videos = renren.search(name);	
		List<VideoEpisode> episodes = new ArrayList<VideoEpisode>();
		
		if(videos.size() > 0) {
			episodes = Optional.ofNullable(videos.get(0).getDownloadListUrl())
					.map(url -> renren.getEpisodeList(url))
					.orElse(episodes);
		}
		
		return episodes.stream()
			.map(episode -> convert(anime, episode))
			.collect(Collectors.toList());
	}

	
	private AnimeEpisode convert(Anime anime, VideoEpisode episode) {
		AnimeEpisode animeEpisode = new AnimeEpisode();
		
		animeEpisode.setAnime(anime);
		animeEpisode.setScratchTime(new Date());
		animeEpisode.setUrl(episode.getDownloadUrl());
		animeEpisode.setNumber(episode.getNum());
		return animeEpisode;
	}
	
}
