package scratch.model.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class AnimeAlias implements Serializable {

	private Long animeId;

	private Long hostId;

	private String name;

	public AnimeAlias() {}

	public AnimeAlias(Long animeId, Long hostId, String name) {
		this.animeId = animeId;
		this.hostId = hostId;
		this.name = name;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AnimeAlias{" +
				"animeId=" + animeId +
				", hostId=" + hostId +
				", name='" + name + '\'' +
				'}';
	}
}