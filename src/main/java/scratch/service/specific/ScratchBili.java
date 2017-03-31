package scratch.service.specific;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rory.regex.ParameterURL;

import scratch.dao.SearchInfoDao;
import scratch.dao.SearchTagDao;
import scratch.model.SearchInfo;
import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.model.dictionary.SearchType;
import scratch.service.SearchKeywordService;

@Transactional
@Service
public class ScratchBili {
	
	//B站 检索page最大50页，其余数据不提供
	private static String SEARCHURL = "http://search.bilibili.com/all";
	
	private static Logger log = Logger.getLogger(ScratchBili.class);
	
	@Autowired
	private SearchInfoDao searchInfoDao;
	
	@Autowired
	private SearchTagDao tagDao;
	
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
		Map<String, String> param = new HashMap<String, String>();
		
		//读取标签信息
		SearchTag tag = tagDao.get(SearchTag.class, tagId);
		//检索关键字列表
		keywordList = tag.getSearchKeyWords();
		if(keywordList == null) return;
		//获取类型信息
		SearchType type = tag.getType();
		//开始构建url参数信息
		if(type != null && !type.getCode().equals(0)) {
			param.put("tids_1", type.getCode().toString());
		}
		param.put("order", "totalrank");
		//拼接链接，搜索每个链接的数据
		for(SearchKeyword k:keywordList){
			boolean jump = false;
			Date searchDate = k.getLastSearchTime();
			//根据关键字，拼接生成URL
			param.put("keyword", k.getKeyword());
			List<URL> urlList = listURL(param);
			for(URL u : urlList) {
				List<SearchInfo> list = new ArrayList<SearchInfo>();
				//打印搜索链接
				log.debug("正在读取URL：" + u.toString());
				//读取url中的元素信息
				try {
					list = scratcchBiliInfo.readInfos(u);
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug("已抓取到数据：" + list);
				//设置默认值
				for(SearchInfo info : list) {
					/*log.debug("上次抓取的日期：" + searchDate + "; 数据发布的日期" + info.getPubDate());
					//时间不对，时间应该缩到日期比较
					if(searchDate != null && searchDate.after(info.getPubDate())) {
						log.debug("抓取的数据过期，数据即将作废");
						jump = true;
						break;
					}*/
					log.debug("开始设置数据基本信息");
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
		log.debug("即将保存数据");
		updateInfo(searchInfoList, keywordList);
	}

	private List<URL> listURL(Map<String, String> param) {
		List<URL> list = new ArrayList<URL>();
		ParameterURL pURL = new ParameterURL(SEARCHURL);
		//设置URL参数信息
		for(Entry<String, String> entry : param.entrySet()) {
			pURL.setParameter(entry.getKey(), entry.getValue());
		}
		//设置检索页数
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
