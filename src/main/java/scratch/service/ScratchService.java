package scratch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.SearchKeywordDao;

@Service
public class ScratchService {
	
	@Autowired
	private SearchKeywordDao keywordDao;
	
	//判断用户是不是第一次抓取数据
	public boolean alertSearch(Long userId) {
		//有设置关键词且但是关键字一次都没进行过搜素
		int keyCount = keywordDao.countByUser(userId);
		int keyNoSearchCount = keywordDao.countNoSearchByUser(userId);
		if(keyCount > 0 && keyCount == keyNoSearchCount) {
			return true;
		}
		return false;
	}
	
}
