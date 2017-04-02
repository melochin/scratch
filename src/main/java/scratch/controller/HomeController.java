package scratch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import scratch.bilibili.service.VideoTypeService;

@Controller
public class HomeController {
	
	@Autowired
	private VideoTypeService typeSerivce;
	
	/**
	 * 显示主页
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/", "/search"}, method=RequestMethod.GET)
	public ModelAndView mainPage(Model model){
		model.addAttribute("types", typeSerivce.list(new Integer(1)));
		return new ModelAndView("index");
	}
	
	/**
	 * 显示信息
	 * @return
	 */
	@RequestMapping(value="/common/message", method=RequestMethod.GET)
	public ModelAndView message() {
		return new ModelAndView("common_message");
	}
	
}
