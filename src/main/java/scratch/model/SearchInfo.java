package scratch.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="search_info")
public class SearchInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="infoId")
	private Long infoId;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name="searchId")
	private SearchKeyword keyword;
	
	@Column(name="title", length=255)
	private String title;
	
	@Column(name="pic", length=255)
	private String pic;
	
	@Column(name="url", length=255)
	private String url;
	
	private Date pubDate;
	
	@Column(name="status")
	private Integer status;
	
	@Transient
	private String shortName;

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public SearchKeyword getKeyword() {
		return keyword;
	}

	public void setKeyword(SearchKeyword keyword) {
		this.keyword = keyword;
	}

	public String getShortName() {
		return title.substring(0, (title.length() >= 45 ? 45 : title.length()));
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((infoId == null) ? 0 : infoId.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((pic == null) ? 0 : pic.hashCode());
		result = prime * result + ((pubDate == null) ? 0 : pubDate.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchInfo other = (SearchInfo) obj;
		if (infoId == null) {
			if (other.infoId != null)
				return false;
		} else if (!infoId.equals(other.infoId))
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (pic == null) {
			if (other.pic != null)
				return false;
		} else if (!pic.equals(other.pic))
			return false;
		if (pubDate == null) {
			if (other.pubDate != null)
				return false;
		} else if (!pubDate.equals(other.pubDate))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "title:" + title + "\n" +
			"href:" + url + "\n" + 
			"pubdate:" + pubDate + "\n";
	}
	
}
