package scratch.api.bilibili;

import java.util.Date;

public class Video {
	// av id
	private Long avid;
	// 视频类型
	private Integer type;
	
	private String title;
	
	private String url;
	
	private String picUrl;
	
	private String uploader;
	
	private Long uploaderId;
	
	private Date createDate;
	
	private Date updateDate;
	
	private Integer duration;
	
	private Integer play;
	
	private String description;
	
	public Video() {}
	
	public Video(Long avid) {
		this.avid = avid;
	}

	public Long getAvid() {
		return avid;
	}

	public void setAvid(Long avid) {
		this.avid = avid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public Long getUploaderId() {
		return uploaderId;
	}

	public void setUploaderId(Long uploaderId) {
		this.uploaderId = uploaderId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getPlay() {
		return play;
	}

	public void setPlay(Integer play) {
		this.play = play;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Video [avid=" + avid + ", type=" + type + ", title=" + title + ", url=" + url + ", picUrl=" + picUrl
				+ ", uploader=" + uploader + ", uploaderId=" + uploaderId + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", duration=" + duration + ", play=" + play + ", description="
				+ description + "]";
	}
	
}