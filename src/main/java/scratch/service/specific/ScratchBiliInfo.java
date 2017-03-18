package scratch.service.specific;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rory.regex.RegexMatch;

import scratch.model.SearchInfo;

/**
 * 执行具体的网页数据抓取
 * @author Admin
 *
 */
@Service
public class ScratchBiliInfo {
	
	/**
	 * http://search.bilibili.com/all?keywrod=?
	 * @param html
	 * @return
	 * @throws Exception
	 */
	public  List<SearchInfo> readInfos(URL url) throws Exception{
			
			List<SearchInfo> searchInfos = new ArrayList<SearchInfo>();
			RegexMatch regexMatch = new RegexMatch(url);
			
			//过滤信息
			List<String> infoList = regexMatch.findTag("li")
					.filter("video matrix")
					.getResultList();
			
			for(String info:infoList){
				RegexMatch infoMatch = new RegexMatch(info);
				SearchInfo searchInfo = new SearchInfo();
				//获取title
				String title = infoMatch.findTag("a")
						.filter("class=\"title\"")
						.getAttribute("title")
						.getReuslt();
				
				//获取url超链接
				String href = infoMatch.findTag("a")
						.filter("class=\"title\"")
						.getAttribute("href")
						.getReuslt();
				if(href != null) {
					href = href.split("\\?")[0];
				}
				
				//获取上传日期
				String pubDate = infoMatch.findTag("span")
						.filter("class=\"so-icon time\"")
						.intercept("</i>", "</span>", ".*")
						.getReuslt();
				
				//获取图片链接
				String img = infoMatch.findTag("div")
						.filter("class=\"img\"")
						.findTag("img")
						.getAttribute("src")
						.getReuslt();
				
				searchInfo.setTitle(title);
				searchInfo.setUrl(href);
				searchInfo.setPic(img);
				
				if(!pubDate.isEmpty()){
					try{
						searchInfo.setPubDate(java.sql.Date.valueOf(pubDate.trim()));
					}catch(java.lang.IllegalArgumentException e){
						continue;
					}
				}
				
				searchInfos.add(searchInfo);
			}
			 return searchInfos;
		}
}
