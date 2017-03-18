package scratch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	/**
	 * 显示主页
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView mainPage(){
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
