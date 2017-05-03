package scratch.dao.inter;

import java.sql.Timestamp;

import scratch.model.SearchKeyword;

public interface ISearchKeywordDao {

	void save(SearchKeyword keyword);
	
	void modifySearchTime(long id, Timestamp searchTime);
	
	void listByTagId(long tagId);
	
	int countByUser(Long userId);
	
	int countNoSearchByUser(Long userId);
}
