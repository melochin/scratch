package scratch.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

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

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.bilibili.model.Video;
import scratch.bilibili.service.VideoService;
import scratch.model.User;
import scratch.service.PageBean;
import scratch.service.SearchTagService;
import scratch.support.SessionSupport;

@Controller
public class BiliVideoController {

	@Autowired
	private VideoService videoService;
	
	@Autowired
	private SearchTagService tagService;
	
	@RequestMapping(value="videos/{type}", method=RequestMethod.GET)
	public ModelAndView listViodes(@PathVariable("type") Long type, Model model) {
		model.addAttribute("type", type);
		return new ModelAndView("videos");
	}
	
	@RequestMapping(value="follow", method=RequestMethod.GET)
	public ModelAndView followVideos(Model model) {
		User user = SessionSupport.getUser();
		model.addAttribute("tags", tagService.listByUserId(user.getUserId()));
		return new ModelAndView("follow_video");
	}
	
	@RequestMapping(path="/ajax/search", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PageBean<List<Video>> query(@RequestParam(name="keyword", required=false) String keyword, 
			@RequestParam(name="type", required=false) Long type,
			@RequestParam(name="order", required=false) String order,
			@RequestParam(name="page", defaultValue="1") Integer page) throws UnsupportedEncodingException {
		if(keyword != null && !keyword.isEmpty()) {
			keyword = URLDecoder.decode(keyword, "UTF-8");
		}
		return videoService.list(keyword, type, order, page);
	}
	
	@RequestMapping(path="/ajax/follow/videos", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PageBean<List<Video>> getVideosByTag(@RequestParam(value="tag", required=false) Long tag) {
		return videoService.list(tag, 1);
	}
	
}