package scratch.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import scratch.model.SearchTag;

@Repository
public class SearchTagDao extends BasicDao{

	private static String QUERY_BY_USERID = "from SearchTag where user.userId = ?";
	
	
	public List<SearchTag> listByUserId(Long userId) {
		return listByHql(QUERY_BY_USERID, userId);
	}
	
}
