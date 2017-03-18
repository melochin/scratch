package scratch.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("deprecation")
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name="search_keyword")
public class SearchKeyword {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="searchId")
	private Long searchId;
	
	@NotNull
	@Column(name="keyword", length=255)
	private String keyword;
	
	@ManyToOne
	@JoinColumn(name="tagId")
	private SearchTag searchTag;
	
	@Column(name="lastSearchTime")
	private Timestamp lastSearchTime;
	
	@JsonIgnoreProperties("keyword")
	@OneToMany(mappedBy="keyword", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
	private List<SearchInfo> infos;
	
	public SearchKeyword() {}
	
	public SearchKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public SearchTag getSearchTag() {
		return searchTag;
	}

	public void setSearchTag(SearchTag searchTag) {
		this.searchTag = searchTag;
	}

	public Long getSearchId() {
		return searchId;
	}

	public void setSearchId(Long searchId) {
		this.searchId = searchId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Timestamp getLastSearchTime() {
		return lastSearchTime;
	}

	public void setLastSearchTime(Timestamp lastSearchTime) {
		this.lastSearchTime = lastSearchTime;
	}
	
	public List<SearchInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<SearchInfo> infos) {
		this.infos = infos;
	}

	public void addInfo(SearchInfo info) {
		info.setKeyword(this);
		infos.add(info);
	}
	
	public void removeInfo(SearchInfo info) {
		infos.remove(info);
		info.setKeyword(null);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((lastSearchTime == null) ? 0 : lastSearchTime.hashCode());
		result = prime * result + ((searchId == null) ? 0 : searchId.hashCode());
		result = prime * result + ((searchTag == null) ? 0 : searchTag.hashCode());
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
		SearchKeyword other = (SearchKeyword) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (lastSearchTime == null) {
			if (other.lastSearchTime != null)
				return false;
		} else if (!lastSearchTime.equals(other.lastSearchTime))
			return false;
		if (searchId == null) {
			if (other.searchId != null)
				return false;
		} else if (!searchId.equals(other.searchId))
			return false;
		if (searchTag == null) {
			if (other.searchTag != null)
				return false;
		} else if (!searchTag.equals(other.searchTag))
			return false;
		return true;
	}

	@Override
	public String toString(){
		return "searchId:" + searchId +
				" keyword:" + keyword + 
				" tagId:" + searchTag.getTagId();
	}
	
}
