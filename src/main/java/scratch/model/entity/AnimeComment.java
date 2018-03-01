package scratch.model.entity;

import java.io.Serializable;
import java.util.Date;

public class AnimeComment implements Serializable {

	private Long animeId;

	private Long userId;

	private String username;

	private String comment;

	private Date date;

	public AnimeComment() {
	}

	public AnimeComment(Long animeId, String comment) {
		this.animeId = animeId;
		this.comment = comment;
	}

	public AnimeComment(Long animeId, Long userId, String comment) {
		this.animeId = animeId;
		this.userId = userId;
		this.comment = comment;
	}

	public Long getAnimeId() {
		return animeId;
	}

	public void setAnimeId(Long animeId) {
		this.animeId = animeId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AnimeComment that = (AnimeComment) o;

		if (animeId != null ? !animeId.equals(that.animeId) : that.animeId != null) return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
		return comment != null ? comment.equals(that.comment) : that.comment == null;
	}

	@Override
	public int hashCode() {
		int result = animeId != null ? animeId.hashCode() : 0;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (comment != null ? comment.hashCode() : 0);
		return result;
	}
}
