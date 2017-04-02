package scratch.bilibili.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import scratch.model.dictionary.Dictionary;

@Entity								//指定实体，用于扫描
@Table(name="bili_video_type")		//指定表名，用于映射
public class VideoType extends Dictionary {

	@OneToMany(mappedBy="parentType", fetch=FetchType.LAZY)
	private List<VideoType> childTypes;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="parent_code")
	private VideoType parentType;
	
	@Column
	private Integer level;

	@Column(name="video_count")
	private Long videoCount;
	
	public VideoType() {}
	
	public VideoType(Long tid) {
		this.setCode(tid);
	}

	public List<VideoType> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<VideoType> childTypes) {
		this.childTypes = childTypes;
	}

	public VideoType getParentType() {
		return parentType;
	}

	public void setParentType(VideoType parentType) {
		this.parentType = parentType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getVideoCount() {
		return videoCount;
	}

	public void setVideoCount(Long videoCount) {
		this.videoCount = videoCount;
	}

	@Override
	public String toString() {
		return "VideoType [name:" + getName() + "]";
	}
	
}
