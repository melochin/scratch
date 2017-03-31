package scratch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.bilibili.model.Video;
import scratch.bilibili.service.VideoService;

@Controller
public class BiliVideoController {

	@Autowired
	private VideoService videoService;
	
	@RequestMapping(name="/search", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Video> query(@RequestParam("keyword") String keyword, 
			@RequestParam(name="type", required=false) Long type,
			@RequestParam(name="order", required=false) String order,
			@RequestParam(name="page", defaultValue="1") Integer page) {
		return videoService.list(keyword, type, order, page);
	}
	
}
