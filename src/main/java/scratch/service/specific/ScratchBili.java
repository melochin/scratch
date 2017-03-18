package scratch.service.specific;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rory.regex.ParameterURL;

import scratch.dao.SearchInfoDao;
import scratch.model.SearchInfo;
import scratch.model.SearchKeyword;
import scratch.service.SearchKeywordService;

@Transactional
@Service
public class ScratchBili {
	
	//B站 检索page最大50页，其余数据不提供
	private static String SEARCHURL = "http://search.bilibili.com/all?keyword=:keyword&order=:order&page=:page";
	
	@Autowired
	private SearchInfoDao searchInfoDao;
	
	@Autowired
	private SearchKeywordService keywordService;
	
	@Autowired
	private ScratchBiliInfo scratcchBiliInfo;
	
	/**
	 * 根据标签信息，检索关键字
	 * @param tagId
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void run(long tagId) throws MalformedURLException, IOException{
		
		List<SearchInfo> searchInfoList = new ArrayList<SearchInfo>();
		List<SearchKeyword> keywordList = new ArrayList<SearchKeyword>();
		
		keywordList = keywordService.listByTagId(tagId);
		if(keywordList == null) return;
		
		//拼接链接，搜索每个链接的数据
		for(SearchKeyword k:keywordList){
			boolean jump = false;
			Date searchDate = k.getLastSearchTime();
			//根据关键字，拼接生成URL
			List<URL> urlList = listURL(k);
			for(URL u : urlList) {
				List<SearchInfo> list = new ArrayList<SearchInfo>();
				//打印搜索链接
				System.out.println("URL：" + u.toString());
				//读取url中的元素信息
				try {
					list = scratcchBiliInfo.readInfos(u);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//设置默认值
				for(SearchInfo info : list) {
					//时间不对，时间应该缩到日期比较
					if(searchDate != null && searchDate.after(info.getPubDate())) {
						jump = true;
						break;
					}
					info.setKeyword(k);
					info.setStatus(0);
					searchInfoList.add(info);
				}
				if(jump) {
					break;
				}
			}
		}
		
		//打印获得信息
		System.out.println(searchInfoList);
		
		updateInfo(searchInfoList, keywordList);
	}

	private List<URL> listURL(SearchKeyword w) {
		List<URL> list = new ArrayList<URL>();
		ParameterURL pURL = new ParameterURL(SEARCHURL);
		pURL.setParameter("keyword", w.getKeyword());
		pURL.setParameter("order", "pubdate");
		for(int p=0; p<10; p++){
			pURL.setParameter("page", String.valueOf(p));
			list.add(pURL.getURL());
		}
		return list;
	}
	
	public void updateInfo(List<SearchInfo> searchInfos, List<SearchKeyword> searchKeywords) {
		//将检索到的数据保存到数据库
		for(SearchInfo searchInfo:searchInfos){
			if(searchInfoDao.isExistByUrl(searchInfo.getUrl())){
				continue;
			}
			searchInfoDao.save(searchInfo);
		}
		
		//更新检索时间
		Timestamp curTime = new Timestamp(System.currentTimeMillis());
		for(SearchKeyword searchKeyword:searchKeywords){
			keywordService.modifySearchTime(searchKeyword.getSearchId(), curTime);
		}
	}
}
