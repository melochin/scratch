package scratch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.SearchInfoDao;
import scratch.dao.SearchTagDao;
import scratch.model.SearchInfo;
import scratch.model.SearchTag;
import scratch.model.SearchTagInfo;

@Service
public class SearchInfoService {

	@Autowired
	private SearchInfoDao infoDao;
	
	@Autowired
	private SearchTagDao tagDao;
	
	@Autowired
	private Page page;
	
	//∑÷“≥≤È—Ø
	public List<SearchInfo> listByTag(Long tagId, Integer curPage) {
		page.setCurPage(curPage);
		return infoDao.listByTag(tagId, page);
	}
	
	
	public List<SearchInfo> list(){
		return infoDao.list(SearchInfo.class);
	}
	
	public List<SearchTagInfo> listSome(Long userId) {
		List<SearchTagInfo> tagInfos = new ArrayList<SearchTagInfo>();
		List<SearchTag> list = tagDao.listByUserId(userId);
		for(SearchTag tag : list) {
			List<SearchInfo> infos = infoDao.listByTagId(tag.getTagId(), 0);
			System.out.println(infos.size());
			tagInfos.add(new SearchTagInfo(tag, infos));
		}
		return tagInfos;
	}
	
	public List<SearchInfo> getNormal(){
		return infoDao.list(new Integer[]{0,1});
	}
	
/*	public List<SearchInfo> loadByTagId(long tagId){
		return searchInfoDao.listByTagId(tagId, new Integer[]{0,1});
	}*/
	
	public void updateStatus(SearchInfo info){
		infoDao.updateStatus(info.getInfoId(), 1);
		return;
	}
	
	public void modifyStatus(Long searchId, SearchInfoStatus status){
		infoDao.updateStatus(searchId, status.ordinal());
		return;
	}
	
}
