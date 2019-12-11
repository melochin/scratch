package scratch.api.renren;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.*;
import scratch.api.exception.ParseException;
import scratch.support.web.HttpConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import scratch.support.web.net.Flow;

public class RenrenImpl implements Renren, Serializable {

	private final static String HOST = "http://www.zmz2019.com";

	private final static String SEARCH_URL = HOST + "/search/index?keyword={1}";

	private final static String RESOURCE_URL = HOST + "/resource/index_json/rid/{1}/channel/tv";

	private final static String RESOURCE_LIST_URL = "http://got001.com/api/v1/static/resource/detail?code={1}";

	/**
	 * 发起搜索请求
	 * 1. 获取请求结果列表
	 * 2. 对每个元素进行遍历，获取具体的下载地址
	 */
	@Override
	public List<Video> search(String keyword) {
		String html = new HttpConnection().connect(SEARCH_URL, keyword);
		
		Document document = Jsoup.parse(html);
		Elements searchItems = document.select(".search-item");
		if (searchItems.size() == 0) throw new ParseException();

		List<Video> videos = searchItems.stream()
				.map(item -> doSearchItem(item))
				.collect(Collectors.toList());
		
		return videos;
	}
	
	/**
	 * 解析'.search-item'，获取Video基本信息
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
		if(index <= 0) return video;

		long id = Long.parseLong(video.getResourceUrl().substring(index + "resource/".length()));
		try {
			String downloadListUrl = getDownloadlistUrl(id);
			video.setDownloadListUrl(downloadListUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return video;
	}

	/**
	 * 从javascript中，获取资源分享地址
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private String getDownloadlistUrl(Long id) throws IOException {
		String js = new HttpConnection().connect(RESOURCE_URL, id);
		// 从js中获取json
		String target = "index_info=";
		String json = js.substring(js.indexOf(target) + target.length());
		// 转换json -> map
		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(json, Map.class);
		// 获取资源分享地址
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
		// get code
		int startIndex = downloadListUrl.indexOf("code=") + "code=".length();
		String code = downloadListUrl.substring(startIndex);

		String json = new HttpConnection().connect(RESOURCE_LIST_URL, code);

		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

		List<VideoEpisode> videoEpisodes = new ArrayList<>();

		JsonArray elements = jsonObject.getAsJsonObject("data").getAsJsonArray("list");
		for(int i=0; i<elements.size(); i++) {

			String season = elements.get(i).getAsJsonObject().get("season_cn").getAsString();
			JsonArray urls = elements.get(i).getAsJsonObject().getAsJsonObject("items").getAsJsonArray("APP");
			for(int j=0; j<urls.size(); j++) {
				JsonObject url = urls.get(j).getAsJsonObject();
				VideoEpisode videoEpisode = new VideoEpisode();
				videoEpisode.setSeason(season);
				videoEpisode.setNum(url.get("episode").getAsString());
				JsonArray files = url.getAsJsonArray("files");
				for(int k=0; k<files.size(); k++) {
					videoEpisode.addDownloadUrl(files.get(k).getAsJsonObject().get("address").toString());
				}
				videoEpisodes.add(videoEpisode);
			}
		}

		videoEpisodes = videoEpisodes.stream()
				.filter((v) -> v != null)
				.collect(Collectors.toList());

		return videoEpisodes;
	}

	@Override
	public boolean isValidate(String url) {
		boolean validate = false;
		try {
			validate = new Flow().get(url).result(html -> !html.contains("链接不存在"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return validate;
	}

	
}
