package scratch.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.User;
import scratch.service.PageBean;
import scratch.service.SearchInfoService;
import scratch.service.SearchTagService;
import scratch.support.GlobalSession;

@Controller
public class SearchInfoController {
	
	@Autowired
	private SearchTagService tagService;
	
	@Autowired
	private SearchInfoService infoService;
	
	/**
	 * 读取SearchTag及其对应的SearchInfo信息，以JSON形式返回
	 * 但每个SearchTag只读取20个SearchInfo
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List getInfos(HttpSession session) {
		//return tagService.list();
		User user = (User)session.getAttribute(GlobalSession.USER);
		return infoService.listSome(user.getUserId());
	}
	
	/**
	 * 读取SearchInfo信息，以JSON形式返回
	 * 根据userId、tagId、page决定返回的数据
	 * @param tag	等于0时，读取该用户所有的searchInfo数据
	 * @param page
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/json/info", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PageBean getInfos(@RequestParam Long tag, 
			@RequestParam(required=false) Integer page, HttpSession session) {
		if(tag == null) {
			tag = (long) 0;
		}
		User user = (User)session.getAttribute(GlobalSession.USER); 
		return infoService.listByTag(tag, user.getUserId(), page);
	}
	
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public ModelAndView info(@RequestParam("tag") Long tag, Model model) {
		model.addAttribute("tag", tag);
		return new ModelAndView("info");
	}
	
	
/*	
	@RequestMapping(value="/json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map getInfos(HttpSession session) {
		//return tagService.list();
		User user = (User)session.getAttribute(GlobalSession.USER);
		return infoService.listSome(user.getUserId());
	}
	
*/	
/*	@Autowired
	private SearchInfoService searchInfoService;

	//更改信息的状态
	@RequestMapping("/searchIno/status/{status}")
	public String modifyStatus(@PathVariable("status") String status, 
			@RequestParam("searchId")long searchId){
		
		SearchInfoStatus searchInoStatus = null;
		
		switch (status) {
			case "readed":
				searchInoStatus = SearchInfoStatus.READED;
				break;
			case "unneed":
				searchInoStatus = SearchInfoStatus.UNNEED;
				break;
			default:
				return "";
		}
		
		searchInfoService.modifyStatus(searchId, searchInoStatus);
		
		return "redirect:/";
	}
	
	
	@RequestMapping(value="/updateStatus", method=RequestMethod.POST)
	public String update(@RequestParam(value="infoId") int infoId){
		SearchInfo search = new SearchInfo();
		search.setInfoId(infoId);
		search.setStatus(9);
		searchInfoService.updateStatus(search);
		return "redirect:/index";
	}
	
	//get searchInfo list by json
	@RequestMapping(value="/json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<SearchInfo> getInfos(){
		return searchInfoService.list();
	}
	
	@RequestMapping(value="/index/{tagId}", method=RequestMethod.GET)
	public ModelAndView infoPage(@PathVariable long tagId ){
		Map<String,Object> model = new HashMap<>();
		model.put("searchInfoList", searchInfoService.loadByTagId(tagId));
		return new ModelAndView("infoList", "data", model);
	}*/
	
	
}
