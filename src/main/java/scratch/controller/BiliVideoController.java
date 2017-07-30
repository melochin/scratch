package scratch.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.service.bilibili.VideoService;
import scratch.context.SessionContext;
import scratch.model.User;
import scratch.model.Video;
import scratch.service.SearchTagService;
import scratch.support.service.PageBean;
import scratch.support.web.spring.SessionUtils;

@Controller
public class BiliVideoController {

	@Autowired
	private VideoService videoService;
	
	@Autowired
	private SearchTagService tagService;
	
	private static Logger log = Logger.getLogger(BiliVideoController.class);
	
	@RequestMapping(value="videos/{type}", method=RequestMethod.GET)
	public ModelAndView listViodes(@PathVariable("type") Long type, Model model) {
		model.addAttribute("type", type);
		return new ModelAndView("videos");
	}
	
	@RequestMapping(value="follow", method=RequestMethod.GET)
	public ModelAndView followVideos(Model model) {
		User user = SessionUtils.getAttribute(SessionContext.USER, User.class);
		model.addAttribute("tags", tagService.listByUserId(user.getUserId()));
		return new ModelAndView("follow_video");
	}
	
	@RequestMapping(path="/ajax/search", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PageBean<Video> query(@RequestParam(name="keyword", required=false) String keyword, 
			@RequestParam(name="type", required=false) Integer type,
			@RequestParam(name="order", required=false) String order,
			@RequestParam(name="page", defaultValue="1") Integer page) throws UnsupportedEncodingException {
		
		long startTime = 0;
		if(log.isDebugEnabled()) {
			startTime = System.nanoTime();
		}
		if(keyword != null && !keyword.isEmpty()) {
			keyword = URLDecoder.decode(keyword, "UTF-8");
		}
		
		PageBean<Video> list = videoService.list(keyword, type, order, page);
		
		if(log.isDebugEnabled()) {
			log.debug("检索结束，共耗时:" + 
					TimeUnit.SECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) + 
					"秒");
		}
		
		return list;
	}
	
	@RequestMapping(path="/ajax/follow/videos", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PageBean<List<Video>> getVideosByTag(@RequestParam(value="tag", required=false) Long tag) {
		return videoService.list(tag, 1);
	}
	
}
