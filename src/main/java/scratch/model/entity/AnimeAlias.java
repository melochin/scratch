package scratch.model.entity;

import java.io.Serializable;

public class AnimeAlias implements Serializable{

	private Long animeId;
	
	private Long hostId;
	
	private String alias;
	
	public Long getAnimeId() {
		return animeId;
	}

	public void setAnimeId(Long animeId) {
		this.animeId = animeId;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
}
