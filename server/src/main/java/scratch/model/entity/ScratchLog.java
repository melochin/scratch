package scratch.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class ScratchLog {

	private long id;

	private long hostId;

	private long animeId;

	private String name;

	private int type;

	private String message;

	@JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
	private Date time;

	public ScratchLog() {
	}

	public ScratchLog(AnimeAlias animeAlias, int type, String message) {
		this.hostId = animeAlias.getHostId();
		this.animeId = animeAlias.getAnimeId();
		this.name = animeAlias.getName();
		this.type = type;
		this.message = message;
		this.time = new Date(System.currentTimeMillis());
	}


	public long getId() {
		return id;
	}

	public long getHostId() {
		return hostId;
	}

	public void setHostId(long hostId) {
		this.hostId = hostId;
	}

	public long getAnimeId() {
		return animeId;
	}

	public void setAnimeId(long animeId) {
		this.animeId = animeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
