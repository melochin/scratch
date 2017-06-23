package scratch.api.bilibili;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.model.Video;
import scratch.support.web.HttpConnection;

public class BilibiliImpl implements Bilibili{

	private final static Logger log = Logger.getLogger(BilibiliImpl.class);
	
	private final static String videoJsonUrl = "http://api.bilibili.com"
			+ "/archive_rank/getarchiverankbypartion?"
			+ "callback=callback&type=jsonp&tid={typeCode}&pn={page}";
	
	private final static String VEDIO_URL = "http://www.bilibili.com/video/av";
	
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
		
		String url = UriComponentsBuilder.fromUriString(videoJsonUrl).build()
				.expand(videoType.getCode(), page).toUriString();
		
		if(log.isDebugEnabled()) {
			log.debug("get url:" + url);
		}	
		
		String html = new HttpConnection().connect(url);
		List<Video> videoList = new ArrayList<Video>();
		
		format.setLenient(false);
		//处理HTML文本
		html = html.substring(9, html.length()-2);
		
		//获取JSON处理工具
		JsonNode tree = null;
		JsonNode videos = null;
		try {
			tree = new ObjectMapper().readTree(html);
			videos = tree.findValue(PARENT_NODE)
					.findValue(VEDIOS);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(videos == null) return null;
		
		//解析数据
		for(JsonNode v : videos) {
			Long avid = v.findValue(AV).asLong();
			if(avid == 0) continue;
			String avUrl = VEDIO_URL + avid;
			Integer tid = v.findValue(TYPE).asInt();
			String title = v.findValue(TITLE).asText();
			String pic = v.findValue(PIC).asText();
			String create = v.findValue(CREATE_TIME).asText();
			Integer play = v.findValue(PLAY).asInt();
			Integer duration = v.findValue(DURATION).asInt();
			String description = v.findValue(DESC).asText();
			Date createTime = null;
			try{
				createTime = format.parse(create);
			}catch (Exception e) {
				createTime = null;
			}
			String uploader = v.findValue(UPLOADER).asText();
		
			//构造Video
			Video video = new Video();
			video.setAvid(avid);
			video.setUrl(avUrl);
			video.setType(new scratch.model.VideoType(tid));
			video.setTitle(title);
			video.setPicUrl(pic);
			video.setUploader(uploader);
			video.setCreateDate(createTime);
			video.setUpdateDate(new Date());
			video.setPlay(play);
			video.setDuration(duration);
			video.setDescription(description);
			
			videoList.add(video);
		}
		
		return videoList;
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
		
		String url = UriComponentsBuilder.fromUriString(videoJsonUrl).build()
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

}
