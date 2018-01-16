package scratch.api.renren;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

		int index = video.getResourceUrl().indexOf("resource/");
		if(index > 0) {
			long id = Long.parseLong(video.getResourceUrl().substring(index + "resource/".length()));
			String url = HOST + "/resource/index_json/rid/" + id + "/channel/tv";
			try {
				String downloadListUrl = getDownloadlistUrl(url);
				video.setDownloadListUrl(downloadListUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return video;
	}

	/**
	 * 从javascript中，获取资源分享地址
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getDownloadlistUrl(String url) throws IOException {
		String target = "index_info=";
		String html = new HttpConnection().connect(url);
		int index = html.indexOf(target);
		String json = html.substring(index + target.length());
		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(json, Map.class);
		return parseResourceUrl((String) map.get("resource_content"));
	}

	/**
	 * 解析json中的html,获取资源分享地址
	 * @param html
	 * @return
	 */
	private String parseResourceUrl(String html) {
		Document document = Jsoup.parse(html);
		Element a = document.select(".view-res-list").select("a").select(".f3").get(0);
		String href = a.attr("href");
		return href;
	}

	/**
	 * 根据下载链接，获取所有的更新信息
	 */
	@Override
	public List<VideoEpisode> getEpisodeList(String downloadListUrl) {
		String html = new HttpConnection().connect(downloadListUrl);
		Document document = Jsoup.parse(html);
		Elements lis = document.select("#APP").select("li.mui-col-xs-4");

		List<VideoEpisode> videoEpisodes = lis.stream()
				.map(ele -> doEpisodeUrl(ele))
				.collect(Collectors.toList());
		videoEpisodes = videoEpisodes.stream().filter((v) ->{
			if(v!=null) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());

		return videoEpisodes;
	}
	
	/**
	 * 解析资源分享页面的DOM元素，提取百度云的下载链接
	 * @param ele
	 * @return
	 */
	private VideoEpisode doEpisodeUrl(Element ele) {
		VideoEpisode videoEpisode = new VideoEpisode();

		Elements els = ele.select("div.desc");
		if(els.size() == 0) return null;
		String number = els.text();

		videoEpisode.setNum(number);
		ele.select("a").forEach((a) -> {
			String href = a.attr("href");
			if(href.indexOf("baidu") > 0) {
				videoEpisode.setDownloadUrl(a.attr("href"));
			}
		});

		return videoEpisode;
	}
	
}
