package scratch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.SearchTypeDao;
import scratch.model.dictionary.SearchType;

@Transactional
@Service
public class SearchTypeService {

	@Autowired
	private SearchTypeDao typeDao;
	
	public List<SearchType> list() {
		return typeDao.list(SearchType.class);
	}
}
