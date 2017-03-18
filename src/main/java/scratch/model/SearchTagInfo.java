package scratch.model;

import java.util.List;

public class SearchTagInfo {
	
	private SearchTag searchTag;
	
	private List<SearchInfo> searchInfos;

	public SearchTagInfo() {}
	
	public SearchTagInfo(SearchTag searchTag, List<SearchInfo> searchInfos) {
		super();
		this.searchTag = searchTag;
		this.searchInfos = searchInfos;
	}

	public SearchTag getSearchTag() {
		return searchTag;
	}

	public void setSearchTag(SearchTag searchTag) {
		this.searchTag = searchTag;
	}

	public List<SearchInfo> getSearchInfos() {
		return searchInfos;
	}

	public void setSearchInfos(List<SearchInfo> searchInfos) {
		this.searchInfos = searchInfos;
	}

}
