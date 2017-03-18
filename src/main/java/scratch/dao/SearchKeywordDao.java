package scratch.dao;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.SearchKeyword;

@Repository
@Transactional
public class SearchKeywordDao extends BasicDao{

	private Session session;
	
	private static String SEARCHBYTAG = "from SearchKeyword where tagId = ? ";
	
	
	public void save(SearchKeyword w){
		session = sessionFactory.getCurrentSession();
		SearchKeyword newWord = null;
		
		long searchId = w.getSearchId();
		if(searchId == 0){
			newWord = w;
		}else{
			newWord = (SearchKeyword) session.load(SearchKeyword.class, searchId);
			newWord.setKeyword(w.getKeyword());
		}
		session.saveOrUpdate(newWord);
	}
	
	public void modifySearchTime(long id, Timestamp searchTime){
		session = sessionFactory.getCurrentSession();
		SearchKeyword word = (SearchKeyword)session.load(SearchKeyword.class, id);
		word.setLastSearchTime(searchTime);
		session.update(word);
	}
	
	public List<SearchKeyword> listByTagId(long tagId){
		return listByHql(SEARCHBYTAG, tagId);
	}
	
}
