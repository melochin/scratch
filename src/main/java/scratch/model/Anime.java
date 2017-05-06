package scratch.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class Anime {

	private Long id;
	
	@NotBlank(message="名称不能为空")
	private String name;
	
	private String alias;
	
	private String pic;
	
	private String description;
	
	private Date publishMonth;
	
	private Boolean finished;
	
	private Integer episodeNo;

	public Anime() {}
	
	public Anime(Long id) {
		this.id = id;
	}
	
	public Anime(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Date getPublishMonth() {
		return publishMonth;
	}

	public void setPublishMonth(Date publishMonth) {
		this.publishMonth = publishMonth;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Integer getEpisodeNo() {
		return episodeNo;
	}

	public void setEpisodeNo(Integer episodeNo) {
		this.episodeNo = episodeNo;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Anime [id=" + id + ", name=" + name + ", alias=" + alias + ", pic=" + pic + ", description="
				+ description + ", publishMonth=" + publishMonth + ", finished=" + finished + ", episodeNo=" + episodeNo
				+ "]";
	}
	
}
