package scratch.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import scratch.dao.SearchTagDao;
import scratch.model.SearchTag;

@Transactional
@Service
public class SearchTagService {
	
	@Autowired
	private SearchTagDao searchTagDao;
	
	public List<SearchTag> list() {
		return searchTagDao.list(SearchTag.class);
	}
	
	public List<SearchTag> listByUserId(Long userId) {
		return searchTagDao.listByUserId(userId);
	}
	
	@Validated
	public void update(@NotNull SearchTag tag) {
		searchTagDao.saveOrUpdate(tag);
	}
	
	public void delete(SearchTag tag) {
		searchTagDao.remove(tag, tag.getTagId());
	}
	
}
