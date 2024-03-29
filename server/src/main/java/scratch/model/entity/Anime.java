package scratch.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

public class Anime implements Serializable{

	private Long id;

//	@NotBlank(message="名称不能为空")
	private String name;
	
	private String pic;
	
	private String description;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date publishMonth;
	
	private Boolean finished;
	
	private Integer episodeNo;
	
	private String type;

	private List<AnimeAlias> aliases;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<AnimeAlias> getAliases() {
		return aliases;
	}

	public void setAliass(List<AnimeAlias> aliases) {
		this.aliases = aliases;
	}

}
