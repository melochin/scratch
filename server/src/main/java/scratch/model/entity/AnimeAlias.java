package scratch.model.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class AnimeAlias implements Serializable{

	private Long hostId;

	private List<String> names;

	public AnimeAlias() {}

	public AnimeAlias(Long hostId, String... names) {
		this.hostId = hostId;
		this.names = Arrays.asList(names);
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

}
