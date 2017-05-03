package scratch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.service.bilibili.VideoTypeService;

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
	public ModelAndView message(RedirectAttributes ra, Model model) {
		model.addAllAttributes(ra.getFlashAttributes());
		return new ModelAndView("common_message");
	}
	
}
