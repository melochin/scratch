package scratch.bilibili.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.model.Video;
import scratch.dao.SearchTagDao;
import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.service.Page;
import scratch.service.PageBean;
import scratch.support.SessionSupport;

@Service
public class VideoService {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private SearchTagDao tagDao;
	
	@Autowired
	private Page page;
	
	public PageBean<List<Video>> list(Long type, String order, Integer pageNo) {
		return list(null, type, order, pageNo);
	}
	
	public PageBean<List<Video>> list(String keyword, Long type, String order, Integer pageNo) {
		page.setCurPage(pageNo);
		return new PageBean<List<Video>>(videoDao.list(keyword, type, order, page), page);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageBean<List<Video>> list(Long tagId, Integer pageNo) {
		page.setCurPage(pageNo);
		
		List<SearchKeyword> searchWords = new ArrayList<SearchKeyword>();
		StringBuilder keyword = new StringBuilder();
		
		Long type = null;
		if(tagId != null && tagId != 0) {
			SearchTag tag = tagDao.get(SearchTag.class, tagId);
			type = tag.getType().getCode();						//��������	
			searchWords = tag.getSearchKeyWords();	//�������ǩ��Ӧ�����йؼ���
		} else {
			if(SessionSupport.getUser() != null) {
				Long userId = SessionSupport.getUser().getUserId();
				List<SearchTag> tags = tagDao.listByUserId(userId);
				for(SearchTag tag : tags) {
					searchWords.addAll(tag.getSearchKeyWords());
				}
			}
		}
		
		for(SearchKeyword w : searchWords) {
			keyword.append(keyword.toString().isEmpty() ? w.getKeyword() : " " + w.getKeyword());
		}
		
		//���ݹؼ��֡���Ƶ���͡��������ڼ���
		return new PageBean(videoDao.list(keyword.toString(), type, VideoDao.ORDER_DATE, page), page);
	}
	
}
