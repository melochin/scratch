package scratch.api.bilibili;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.support.web.HttpConnection;

public class BilibiliImpl implements Bilibili{

	private final static Logger log = Logger.getLogger(BilibiliImpl.class);
	
	private final static String VIDEO_API_URL = "http://api.bilibili.com"
			+ "/archive_rank/getarchiverankbypartion?"
			+ "callback=callback&type=jsonp&tid={typeCode}&pn={page}";
	
	private final static String VEDIO_URL = "http://www.bilibili.com/video/av";
	
	private final static String SEARCH_URL = "http://search.bilibili.com/all"
			+ "?keyword={keyword}&order={order}&page={page}";
	
	private final static String PARENT_NODE = "data";
	
	private final static String VEDIOS = "archives";
	
	private final static String TITLE = "title";
	
	private final static String PIC ="pic";
	
	private final static String AV = "aid";
	
	private final static String TYPE ="tid"; 
	
	private final static String UPLOADER = "author";
	
	private final static String CREATE_TIME = "create";
	
	private final static String PLAY = "play";
	
	private final static String DURATION = "duration";
	
	private final static String DESC = "description";
	
	private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Override
	public List<Video> getVideos(VideoType videoType, int page) {
		List<Video> videoList = new ArrayList<Video>();
		
		String url = UriComponentsBuilder.fromUriString(VIDEO_API_URL).build()
				.expand(videoType.getCode(), page).toUriString();
		String html = new HttpConnection().connect(url);
		String json = html.substring(9, html.length()-2);
		
		log.debug(json);
		
		format.setLenient(false);
		
		//获取JSON处理工具
		JsonNode tree = null;
		JsonNode videos = null;
		try {
			tree = new ObjectMapper().readTree(json);
			videos = tree.findValue(PARENT_NODE).findValue(VEDIOS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		videos.forEach(jsonNode -> {
			Video video = parseVideoJson(jsonNode);
			if(video == null) return;
			videoList.add(video);
		});
		
		return videoList;
	}
	
	private Video parseVideoJson(JsonNode jsonNode) {
		Long avid = jsonNode.findValue(AV).asLong();
		if(avid == 0) return null;
		String avUrl = VEDIO_URL + avid;
		Integer tid = jsonNode.findValue(TYPE).asInt();
		String title = jsonNode.findValue(TITLE).asText();
		String pic = jsonNode.findValue(PIC).asText();
		String create = jsonNode.findValue(CREATE_TIME).asText();
		Integer play = jsonNode.findValue(PLAY).asInt();
		Integer duration = jsonNode.findValue(DURATION).asInt();
		String description = jsonNode.findValue(DESC).asText();
		Date createTime = null;
		try{
			createTime = format.parse(create);
		}catch (Exception e) {
			createTime = null;
		}
		String uploader = jsonNode.findValue(UPLOADER).asText();
	
		//构造Video
		Video video = new Video();
		video.setAvid(avid);
		video.setUrl(avUrl);
		video.setType(tid);
		video.setTitle(title);
		video.setPicUrl(pic);
		video.setUploader(uploader);
		video.setCreateDate(createTime);
		video.setUpdateDate(new Date());
		video.setPlay(play);
		video.setDuration(duration);
		video.setDescription(description);
		return video;
	}
	
	@Override
	public boolean isLogin() {
		return false;
	}
	
	@Override
	public Map<String, Integer> getVideoPageAndCount(VideoType videoType) {
		return getVideoPageAndCount(videoType.getCode());
	}

	@Override
	public Map<String, Integer> getVideoPageAndCount(int videoTypeId) {
		
		int count = 0;
		int pageCount = 0;
		
		Map<String, Integer> map = new HashMap<String,Integer>();
		
		// 默认数量和页数为0
		map.put("count", count);
		map.put("page", pageCount);
		
		String url = UriComponentsBuilder.fromUriString(VIDEO_API_URL).build()
				.expand(videoTypeId, 1).toUriString();
		
		String html = new HttpConnection().connect(url);
		html = html.substring(9, html.length()-2);
		
		ObjectMapper m = new ObjectMapper();
		JsonNode tree = null;
		JsonNode page = null;
		
		try {
			tree = m.readTree(html);
			page = tree.findValue("page");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(page == null) return null;
		count = page.findValue("count").asInt();
		long size = page.findValue("size").asLong();
		if(count == 0 || size == 0) return null;
		pageCount = (int) Math.ceil((double)count / (double)size);
		
		map.put("count", count);
		map.put("page", pageCount);
		
		return map;
	}


	/**
	 * @see #search(String, int)
	 */
	@Override
	public List<Video> search(String keyword) {
		return search(keyword, 1);
	}
	
	/**
	 * 具体数据解析
	 * @see #parseVideoDiv(Element)
	 * @param keyword
	 * @param page
	 * @return
	 */
	public List<Video> search(String keyword, int page) {
		List<Video> videos = new ArrayList<Video>();
		String url = UriComponentsBuilder.fromUriString(SEARCH_URL).build()
				.expand(keyword, "pubdate", page)
				.encode()
				.toUriString();
		String html = new HttpConnection().connect(url);
		log.debug(html);
		
		Document document = Jsoup.parse(html);
		document.select(".video.matrix")
			.forEach(videoDiv -> {
				Video video = parseVideoDiv(videoDiv);
				videos.add(video);
			});
		return videos;
	}
	
	/**
	 * 解析 class=".video matrix" 容器内的数据
	 * @param element
	 * @return
	 */
	private Video parseVideoDiv(Element element) {
		Video video = new Video();
		Element a = element.select(".headline>a").get(0);
		
		String title = Optional.of(a.attr("title")).get();
		video.setTitle(title);
		
		String href = Optional.of(a.attr("href")).get();
		video.setUrl(href);
		
		Element img = element.select(".img>img").get(0);
		String picUrl = Optional.of(img.attr("src")).get(); 
		video.setPicUrl(picUrl);
		
		Element iDate = element.select(".icon-date").get(0);
		String sDate = Optional.of(iDate.nextSibling().toString().trim()).get();
		Date date;
		try {
			date = DateFormat.getDateInstance().parse(sDate);
			video.setUpdateDate(date);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return video;
	}

}
