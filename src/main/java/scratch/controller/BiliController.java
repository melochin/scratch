package scratch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.bilibili.dao.VideoTypeDao;
import scratch.bilibili.model.VideoType;
import scratch.bilibili.service.ScratchService;
import scratch.bilibili.service.VideoTypeService;

/**
 * ��̨ץȡ����
 * @author melochin
 *
 */
@Controller
public class BiliController {

	@Autowired
	private VideoTypeDao typeDao;
	
	@Autowired
	private VideoTypeService typeService;
	
	@Autowired
	private ScratchService scratchService;
	
	@UserRole(Role.Admin)
	@RequestMapping(value="bili/setting")
	public String setting() {
		return "bili_setting";
	}
	
	@RequestMapping(value="bili/ajax/getVideoTypes", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<VideoType> getVideoTyps() {
		return typeDao.list(1);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="bili/ajax/getVideoTypesCount", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map getVideoTypesCount() {
		return typeService.listVideoCount();
	}
	
	@RequestMapping(value="bili/ajax/startService", method=RequestMethod.GET) 
	public void startService() {
		scratchService.scratchVideo();
	}
	
	@RequestMapping(value="bili/ajax/isRun", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> isServiceRun() {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean run = scratchService.isRun();
		map.put("run", run);	
		if(run == false) {
			map.put("record", scratchService.getRecentRecord());
		}
		return map;
	}
	
}
