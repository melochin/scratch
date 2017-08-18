package scratch.model.entity;

public class AnimePush {

	private AnimeEpisode episode;
	
	private User user;
	
	private Integer status;

	public AnimeEpisode getEpisode() {
		return episode;
	}

	public void setEpisode(AnimeEpisode episode) {
		this.episode = episode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
