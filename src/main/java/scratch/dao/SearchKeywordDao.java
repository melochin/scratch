package scratch.dao;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.SearchKeyword;
import scratch.model.User;

@Repository
@Transactional
public class SearchKeywordDao extends BasicDao<SearchKeyword>{

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
	
	public int countByUser(Long userId) {
		Criteria crt = createCriteria(SearchKeyword.class);
		crt.createCriteria("searchTag")
			.add(Restrictions.eq("user", new User(userId)));
		crt.setProjection(Projections.rowCount());
		return Integer.valueOf(crt.uniqueResult().toString());
	}
	
	public int countNoSearchByUser(Long userId) {
		Criteria crt = createCriteria(SearchKeyword.class);
		crt.add(Restrictions.isNull("lastSearchTime"));
		crt.createCriteria("searchTag")
			.add(Restrictions.eq("user", new User(userId)));
		crt.setProjection(Projections.rowCount());
		return Integer.valueOf(crt.uniqueResult().toString());
	}
	
}
