package scratch.model.view;

import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

import java.util.Date;

public class AnimeEpisodeDisplay {

	private AnimeEpisode animeEpisode;

	private Long hot;

	public AnimeEpisodeDisplay() {}

	public AnimeEpisodeDisplay(AnimeEpisode animeEpisode) {
		this.animeEpisode = animeEpisode;
	}

	public Long getId() {
		return animeEpisode.getId();
	}

	public void setId(Long id) {
		animeEpisode.setId(id);
	}

	public Anime getAnime() {
		return animeEpisode.getAnime();
	}

	public void setAnime(Anime anime) {
		animeEpisode.setAnime(anime);
	}

	public String getNumber() {
		return animeEpisode.getNumber();
	}

	public void setNumber(String number) {
		animeEpisode.setNumber(number);
	}

	public String getUrl() {
		return animeEpisode.getUrl();
	}

	public void setUrl(String url) {
		animeEpisode.setUrl(url);
	}

	public Date getScratchTime() {
		return animeEpisode.getScratchTime();
	}

	public void setScratchTime(Date scratchTime) {
		animeEpisode.setScratchTime(scratchTime);
	}

	public Long getHostId() {
		return animeEpisode.getHostId();
	}

	public void setHostId(Long hostId) {
		animeEpisode.setHostId(hostId);
	}

	public Date getSaveTime() {
		return animeEpisode.getSaveTime();
	}

	public void setSaveTime(Date saveTime) {
		animeEpisode.setSaveTime(saveTime);
	}

	public Date getPushTime() {
		return animeEpisode.getPushTime();
	}

	public void setPushTime(Date pushTime) {
		animeEpisode.setPushTime(pushTime);
	}

	public Long getHot() {
		return hot;
	}

	public void setHot(Long hot) {
		this.hot = hot;
	}
}
