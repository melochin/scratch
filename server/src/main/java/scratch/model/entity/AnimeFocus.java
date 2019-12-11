package scratch.model.entity;

import java.io.Serializable;
import java.util.Date;

public class AnimeFocus implements Serializable {

	private Long id;
	
	private Anime anime;
	
	private User user;
	
	private Date lastPushTime;

	public AnimeFocus() {}
	
	public AnimeFocus(Anime anime, User user) {
		this.anime = anime;
		this.user = user;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Anime getAnime() {
		return anime;
	}

	public void setAnime(Anime anime) {
		this.anime = anime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getLastPushTime() {
		return lastPushTime;
	}

	public void setLastPushTime(Date lastPushTime) {
		this.lastPushTime = lastPushTime;
	}

}
