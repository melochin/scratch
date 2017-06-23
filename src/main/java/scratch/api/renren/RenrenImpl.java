package scratch.api.renren;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.support.web.HttpConnection;

public class RenrenImpl implements Renren {

	private final static String HOST = "http://www.zimuzu.tv";
	
	private final static String SEARCH_URL = 
			"http://www.zimuzu.tv/search/index?keyword=";
	
	private final static String DOWNLOADLIST_URL = 
			"http://xiazai002.com/";
	
	/**
	 * 根据关键字获取影视信息
	 */
	@Override
	public List<Video> search(String keyword) {
		String url = SEARCH_URL + keyword;
		String html = new HttpConnection().connect(url);
		
		Document document = Jsoup.parse(html);
		Elements searchItems = document.select(".search-item");
		
		List<Video> videos = searchItems.stream()
				.map(item -> doSearchItem(item))
				.collect(Collectors.toList());
		
		return videos;
	}
	
	/**
	 * 解析DOM元素，获取Video基本信息
	 * @param ele
	 * @return
	 */
	private Video doSearchItem(Element ele) {
		Video video = new Video();
		
		Elements imgs = ele.select(".fl-img");
		video.setImgUrl(imgs.select("img").attr("src"));
		
		Elements infos = ele.select(".fl-info");
		
		video.setResourceUrl(infos.select("a").attr("href"));
		
		video.setTitle(infos.select(".list_title").text());
		
		Date date = new Date(Long.parseLong(infos.select(".f4").select(".time").text()));
		video.setPublishAt(date);
		
		if(video.getResourceUrl().indexOf("resource") >= 0) {
			String url = HOST + video.getResourceUrl();
			String html = new HttpConnection().connect(url);
			
			Document document = Jsoup.parse(html);
			
			for(Element a : document.select(".view-res-list").select("a").select(".f3")) {
				String href = a.attr("href");
				if(href.indexOf(DOWNLOADLIST_URL) >= 0) {
					video.setDownloadListUrl(href);
					break;
				}
			}
		}
		
		return video;
	}
	
	/**
	 * 根据下载链接，获取所有的更新信息
	 */
	@Override
	public List<VideoEpisode> getEpisodeList(String downloadListUrl) {
		String html = new HttpConnection().connect(downloadListUrl);
		Document document = Jsoup.parse(html);
		
		//获取存放下载链接的JSON对象，并转换成map
		Map<?, ?> map = doScriptItems(document.select("script"));
		
		List<VideoEpisode> videoEpisodes = document.select(".res-item[format='APP']")
				.select(".clearfix").stream()
				.map(ele -> doEpisodeUrl(map, ele))
				.collect(Collectors.toList());
		
		return videoEpisodes;
	}

	/**
	 * 解析DOM元素，获取javascript中含有文件信息的部分
	 * @param eles
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<Object, Object> doScriptItems(Elements eles) {
		String FILE_LIST = "file_list";
		Map<Object, Object> map = new HashMap<Object, Object>();

		for(Element scriptItem : eles) {
			
			String script = scriptItem.html();
			int index = script.indexOf(FILE_LIST);
			if(index < 0) continue;
			
			String json = script.substring(index + FILE_LIST.length() + 1, script.length());
			ObjectMapper object = new ObjectMapper();
			try {
				map = object.readValue(json, Map.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return map;
		}
		
		return map;
	}
	
	/**
	 * 解析DOM元素，提取百度云的下载链接
	 * @param map
	 * @param ele
	 * @return
	 */
	private VideoEpisode doEpisodeUrl(Map map, Element ele) {
		VideoEpisode videoEpisode = new VideoEpisode();
		
		String id = ele.attr("itemid");
		//获取百度云链接
		String url = (String) ((Map)map.get(id)).get("102");
		if(StringUtils.isEmpty(url)) {
			return null;
		}
		
		String number = ele.select("b").get(0).text().replaceAll("[^0-9]", "");
		videoEpisode.setDownloadUrl(url);
		videoEpisode.setNum(number);
		return videoEpisode;
	}
	
}
