package scratch.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class AnimeEpisode implements Serializable {

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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date scratchTime;
	
	// 保存时间
	private Date saveTime; 
	
	// 最近一次推送时间
	private Date pushTime;
	
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
	
	public Date getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}

	public Date getPushTime() {
		return pushTime;
	}

	public void setPushTime(Date pushTime) {
		this.pushTime = pushTime;
	}

	@Override
	public String toString() {
		return "AnimeEpisode [id=" + id + ", hostId=" + hostId + ", anime=" + anime + ", number=" + number + ", url="
				+ url + ", scratchTime=" + scratchTime + ", saveTime=" + saveTime + ", pushTime=" + pushTime + "]";
	}

}
