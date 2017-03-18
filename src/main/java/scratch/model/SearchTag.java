package scratch.model;

import java.util.ArrayList;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="search_tag")
public class SearchTag implements Comparable<SearchTag>{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tag_id")
	private Long tagId;
	
	@NotNull
	@Column(name="name", length=255)
	private String tagName;
	
	//级联操作所有，加载方式懒加载
	@JsonIgnore
	@OneToMany(mappedBy="searchTag", cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	private List<SearchKeyword> searchKeyWords = new ArrayList<SearchKeyword>();
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public SearchTag() {}
	
	public SearchTag(Long tagId) {
		this.tagId = tagId;
	}
	
	public SearchTag(String tagName) {
		this.tagName = tagName;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<SearchKeyword> getSearchKeyWords() {
		return searchKeyWords;
	}

	public void setSearchKeyWords(List<SearchKeyword> searchKeyWords) {
		this.searchKeyWords = searchKeyWords;
	}
	
	//SearchKeyword，需要设置hashcode 与 equal
	public void addKeyword(SearchKeyword word) {
		searchKeyWords.add(word);
		word.setSearchTag(this);
	}
	
	public void removeKeyword(SearchKeyword word) {
		searchKeyWords.remove(word);
		word.setSearchTag(null);
	}
	
	@Override
	public int compareTo(SearchTag o) {
		if(this.tagId == o.tagId){
			return 0;
		}else if(this.tagId < o.tagId){
			return -1;
		}else{
			return 1;
		}
	}
	
}
