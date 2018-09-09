package scratch.api.bilibili;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import scratch.support.web.HttpConnection;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import scratch.support.web.net.Flow;

import javax.swing.text.html.Option;


public class BilibiliImpl implements Bilibili{

	private final static Logger log = Logger.getLogger(BilibiliImpl.class);
	
	private final static String VIDEO_API_URL = "http://api.bilibili.com"
			+ "/archive_rank/getarchiverankbypartion?"
			+ "callback=callback&type=jsonp&tid={typeCode}&pn={page}";
	
	private final static String VIDEO_URL = "http://www.bilibili.com/video/av";
	
	private final static String SEARCH_URL = "http://search.bilibili.com/api/search"
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
		String html = new HttpConnection().connect(VIDEO_API_URL, videoType.getCode(), page);
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
	
	private Video parseVideoJson(JsonNode node) {
		Long avid = node.findValue(AV).asLong();
		if(avid == 0) return null;
		String avUrl = VIDEO_URL + avid;

		Integer tid = node.findValue(TYPE).asInt();
		String title = node.findValue(TITLE).asText();
		String pic = node.findValue(PIC).asText();
		String create = node.findValue(CREATE_TIME).asText();
		Integer play = node.findValue(PLAY).asInt();
		Integer duration = node.findValue(DURATION).asInt();
		String description = node.findValue(DESC).asText();
		Date createTime = null;
		try{
			createTime = format.parse(create);
		}catch (Exception e) {
			createTime = null;
		}
		String uploader = node.findValue(UPLOADER).asText();
	
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
	public List<Video> focus(long focusId) {
		String url = "https://space.bilibili.com/ajax/member" +
				"/getSubmitVideos?" +
				"mid={1}&pagesize=100&tid=0&page=1&keyword=&order=pubdate";
		String json = new HttpConnection().connect(url, focusId);
		ObjectMapper mapper = new ObjectMapper();
		List<Video> videos = new ArrayList<>();
		try {
			mapper.readTree(json).get("data").get("vlist").forEach(node -> {
				videos.add(convert(node));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return videos;
	}

	@Override
	public boolean isValidate(String url) {
		boolean validate = false;
		try {
			validate = new Flow().get(url)
					.result(html -> !html.contains("视频去哪了呢"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return validate;
	}

	private Video convert(JsonNode node) {
		Video video = new Video();

		Optional.ofNullable(node.get("aid"))
				.ifPresent(aid -> {
					video.setAvid(aid.asLong());
					video.setUrl(VIDEO_URL + aid);
				});
		Optional.ofNullable(node.get("author"))
				.ifPresent(author -> video.setUploader(author.asText()));
		Optional.ofNullable(node.get("description"))
				.ifPresent(description -> video.setDescription(description.asText()));
		Optional.ofNullable(node.get("pic"))
				.ifPresent(pic -> video.setPicUrl(pic.asText()));
		Optional.ofNullable(node.get("title"))
				.ifPresent(title -> video.setTitle(title.asText()));
		Optional.ofNullable(node.get("typeid"))
				.ifPresent(typeid -> video.setType(typeid.asInt()));

		return video;
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
	 * @see #parseVideo(Map)
	 * @param keyword
	 * @param page
	 * @return
	 */
	public List<Video> search(String keyword, int page) {
		List<Video> videos = new ArrayList<Video>();
		String html = new HttpConnection().connect(SEARCH_URL, keyword, "pubdate", page);
		log.debug(html);
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map map = mapper.readValue(html, Map.class);
			List<Map> list = (List<Map>) ((Map)map.get("result")).get("video");

			list.forEach(obj -> {
				Video video = parseVideo(obj);
				videos.add(video);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
		return videos;
	}
	
	/**
	 * @param map
	 * @return
	 */
	private Video parseVideo(Map map) {
		Video video = new Video();

		String title = (String) map.get("title");
		video.setTitle(title);
		
		String href = (String) map.get("arcurl");
		video.setUrl(href);
		
		String picUrl = (String) map.get("pic");
		video.setPicUrl(picUrl);

		Integer sDate = (Integer) map.get("senddate");
		Date date = new Date(sDate);
		video.setUpdateDate(date);

		return video;
	}

}
