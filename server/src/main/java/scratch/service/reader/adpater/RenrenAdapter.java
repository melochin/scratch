package scratch.service.reader.adpater;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import scratch.api.renren.Renren;
import scratch.api.renren.Video;
import scratch.api.renren.VideoEpisode;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

public class RenrenAdapter implements ScratchAdpater {

	private Renren renren;
	
	public RenrenAdapter(Renren renren) {
		this.renren = renren;
	}
	
	@Override
	public List<AnimeEpisode> readAnimeEpidsodes(Anime anime, String keyword) {
		
		List<Video> videos = renren.search(keyword);
		if(videos.size() == 0) return null;

		String downLoadListUrl = videos.get(0).getDownloadListUrl();
		List<VideoEpisode> episodes = renren.getEpisodeList(downLoadListUrl);

		return episodes.stream()
			.map(episode -> convert(anime, episode))
			.collect(Collectors.toList());
	}

	@Override
	public Long getHostId() {
		return new Long(2);
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
