package scratch.service.bilibili;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageRowBounds;

import scratch.dao.SearchTagDao;
import scratch.dao.inter.IVideoDao;
import scratch.dao.inter.IVideoTypeDao;
import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.model.Video;
import scratch.model.VideoType;
import scratch.support.service.Page;
import scratch.support.service.PageBean;
import scratch.support.web.SessionSupport;

@Service
public class VideoService {

	@Autowired
	private IVideoDao videoDao;
	
	@Autowired
	private IVideoTypeDao typeDao;
	
	@Autowired
	private SearchTagDao tagDao;
	
	@Autowired
	private Page page;
	
	/**
	 * 根据视频类别检索视频信息
	 * @param type
	 * @param order
	 * @param pageNo
	 * @return
	 */
	public PageBean<Video> list(Integer type, String order, Integer pageNo) {
		return list(null, type, order, pageNo);
	}
	
	/**
	 * 根据类别、关键字检索视频信息
	 * @param keyword
	 * @param type
	 * @param order
	 * @param pageNo
	 * @return
	 */
	public PageBean<Video> list(String keyword, Integer type, String order, Integer pageNo) {
		//将搜索关键字转换为List
		List<String> words = new ArrayList<String>();
		if(keyword != null && !keyword.isEmpty()) {
			words = Arrays.asList(keyword.split(" ")); 
		}
		//设置当前页数
		page.setCurPage(pageNo);
		PageRowBounds pageRowBounds = new PageRowBounds(page.getPerPageItem() * (pageNo-1), page.getPerPageItem());
		List<VideoType> videoTypes = typeDao.listById(type);
		List<Integer> types = new ArrayList<>();
		for(VideoType v : videoTypes) {
			types.add(v.getCode());
		}
		List<Video> videos = videoDao.list(words, types, order, pageRowBounds);
		//获取总个数
		page.setTotalItem(pageRowBounds.getTotal());
		return new PageBean<Video>(videos, page);
	}
	
	/**
	 * 根据标签检索视频信息
	 * @param tagId
	 * @param pageNo
	 * @return
	 */
	public PageBean<List<Video>> list(Long tagId, Integer pageNo) {
		
		List<SearchKeyword> searchWords = new ArrayList<SearchKeyword>();
		List<String> words = new ArrayList<String>();
		Integer type = null;
		
		//存在标签ID的情况下，检索标签对应的视频类型和关键字集合
		//否则读取用户的所有标签集
		if(tagId != null && tagId != 0) {
			SearchTag tag = tagDao.get(SearchTag.class, tagId);
			type = tag.getType().getCode();	
			searchWords = tag.getSearchKeyWords();
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
			words.add(w.getKeyword());
		}
		
		//设置当前页数
		page.setCurPage(pageNo);
		PageRowBounds pageRowBounds = new PageRowBounds(page.getPerPageItem() * (pageNo-1), page.getPerPageItem());
	//	List<Video> videos = videoDao.list(words, type, VideoDao.ORDER_DATE,  pageRowBounds);
		//获取总个数
		page.setTotalItem(pageRowBounds.getTotal());
//		return new PageBean<List<Video>>(videos, page);
		return null;
	}
	
}
