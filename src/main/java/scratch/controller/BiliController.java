package scratch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.service.bilibili.VideoTypeService;
import scratch.dao.VideoTypeDao;
import scratch.model.VideoType;

@Controller
public class BiliController {

	@Autowired
	private VideoTypeDao typeDao;
	
	@Autowired
	private VideoTypeService typeService;
	
/*	@Autowired
	private ScratchService scratchService;*/
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting")
	public String setting() {
		return "bili_setting";
	}
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/ajax/getVideoTypes", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<VideoType> getVideoTyps() {
		return typeDao.list(1);
	}
	
	@SuppressWarnings("rawtypes")
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/ajax/getVideoTypesCount", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map getVideoTypesCount() {
		return typeService.listVideoCount();
	}
	
/*	@UserRole(Role.Admin)
	@RequestMapping(value="bili/ajax/startService", method=RequestMethod.GET) 
	public void startService() {
		scratchService.scratchVideo();
	}*/
	
	
/*	@UserRole(Role.Admin)
	@RequestMapping(value="bili/ajax/isRun", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> isServiceRun() {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean run = scratchService.isRun();
		map.put("run", run);	
		if(run == false) {
			map.put("record", scratchService.getRecentRecord());
		}
		return map;
	}*/
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting/type")
	public String listTypes(Model model) {
		List<VideoType> videoTypes = typeService.list();
		model.addAttribute("videoTypes", videoTypes);
		return "bili_video_type";
	}
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting/type/update", method=RequestMethod.POST)
	public String modifyType(VideoType type) {
		typeService.modify(type);
		return "bili_video_type";
	}
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting/type/delete", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,String> deleteType(@RequestParam("code") Integer code) {
		Map<String, String> map = new HashMap<String,String>();
		try {
			typeService.delete(code);
		} catch (Exception e) {
			map.put("error", "存在外键关联无法删除");
		}
		return map;
	}
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting/type/insert", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,String> insertType(VideoType type) {
		Map<String, String> map = new HashMap<String,String>();
		try {
			typeService.insert(type);
		} catch (Exception e) {
			map.put("error", e.getMessage());
		}
		return map;
	}	
}
