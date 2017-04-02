package scratch.bilibili.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="bili_video")
public class Video {

	@Id
	private Long avid;
	
	@ManyToOne
	@JoinColumn(name="tid")
	@JsonIgnoreProperties("childTypes")
	private VideoType type;
	
	@Column
	private String title;
	
	@Column
	private String url;
	
	@Column(name="pic_url")
	private String picUrl;
	
	@Column
	private String uploader;
	
	@Column(name="uploader_id")
	private Long uploaderId;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="update_date")
	private Date updateDate;
	
	//时长：单位秒
	@Column
	private Integer duration;
	
	//播放次数
	@Column
	private Integer play;
	
	//视频描述
	@Column
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

	public VideoType getType() {
		return type;
	}

	public void setType(VideoType type) {
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
				+ ", updateDate=" + updateDate + "]";
	}
	
}
