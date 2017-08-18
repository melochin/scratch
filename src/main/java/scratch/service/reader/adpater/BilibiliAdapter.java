package scratch.service.reader.adpater;

import java.util.List;
import java.util.stream.Collectors;

import scratch.api.bilibili.Bilibili;
import scratch.api.bilibili.Video;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public class BilibiliAdapter implements Adapter {

	private Bilibili bilibili;
	
	public BilibiliAdapter(Bilibili bilibili) {
		this.bilibili = bilibili;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsode(Anime anime) {
		List<Video> videos = bilibili.search(anime.getName());
		return videos.stream()
				.map(video -> convert(anime, video))
				.collect(Collectors.toList());
		
	}
	
	public AnimeEpisode convert(Anime anime, Video video) {
		AnimeEpisode animeEpisode = new AnimeEpisode();
		animeEpisode.setAnime(anime);
		animeEpisode.setUrl(video.getUrl());
		animeEpisode.setNumber(video.getTitle());
		return animeEpisode;
	}

}
