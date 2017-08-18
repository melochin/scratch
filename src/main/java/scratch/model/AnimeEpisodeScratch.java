package scratch.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class AnimeEpisodeScratch {

	// 自增ID
	private Long id;
	
	// 站点ID
	private Long hostId;
	
	//关联对象
	private Anime anime;
	
	//集号
	private String number;
	
	//链接
	private String url;
	
	// 抓取时间
	private Date scratchTime;
	
	// 状态
	private Integer status;
	
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Date getScratchTime() {
		return scratchTime;
	}

	public void setScratchTime(Date scratchTime) {
		this.scratchTime = scratchTime;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AnimeEpisodeScratch [id=" + id + ", hostId=" + hostId + ", anime=" + anime + ", number=" + number
				+ ", url=" + url + ", scratchTime=" + scratchTime + ", status=" + status + "]";
	}

}
