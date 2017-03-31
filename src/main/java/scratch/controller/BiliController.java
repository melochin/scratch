package scratch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.annotations.CreationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.bilibili.dao.VideoTypeDao;
import scratch.bilibili.model.VideoType;
import scratch.bilibili.service.ScratchService;
import scratch.bilibili.service.VideoTypeService;

@Controller
public class BiliController {

	@Autowired
	private VideoTypeDao typeDao;
	
	@Autowired
	private VideoTypeService typeService;
	
	@Autowired
	private ScratchService scratchService;
	
	@RequestMapping(value="bili/setting")
	public String setting() {
		return "bili_setting";
	}
	
	@RequestMapping(value="bili/ajax/getVideoTypes", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<VideoType> getVideoTyps() {
		return typeDao.list(1);
	}
	
	@RequestMapping(value="bili/ajax/getVideoTypesCount", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map getVideoTypesCount() {
		return typeService.listVideoCount();
	}
	
	@RequestMapping(value="bili/ajax/startService", method=RequestMethod.GET) 
	public void startService() {
		scratchService.scratchVideo();
	}
	
	@RequestMapping(value="bili/ajax/isRun", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map isServiceRun() {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean run = scratchService.isRun();
		map.put("run", run);	
		if(run == false) {
			map.put("record", scratchService.getRecentRecord());
		}
		return map;
	}
	
}
