package scratch.service.reader.adpater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import scratch.api.bilibili.Bilibili;
import scratch.api.bilibili.Video;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.support.StringUtils;

public class BilibiliAdapter implements ScratchAdpater {

	private Bilibili bilibili;
	
	public BilibiliAdapter(Bilibili bilibili) {
		this.bilibili = bilibili;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsodes(Anime anime, String keyword) {
		List<Video> videos = new ArrayList<>();
		if(anime == null || keyword == null || keyword.isEmpty()) return null;

		if(StringUtils.isNumber(keyword)) {
			videos.addAll(bilibili.focus(Long.valueOf(keyword)));
		} else {
			videos.addAll(bilibili.search(keyword));
		}

		return videos.stream()
				.map(video -> convert(anime, video))
				.collect(Collectors.toList());
		
	}

	@Override
	public Long getHostId() {
		return new Long(3);
	}

	private AnimeEpisode convert(Anime anime, Video video) {
		AnimeEpisode animeEpisode = new AnimeEpisode();
		animeEpisode.setAnime(anime);
		animeEpisode.setUrl(video.getUrl());
		animeEpisode.setNumber(video.getTitle());
		return animeEpisode;
	}

}
