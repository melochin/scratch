package scratch.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.service.bilibili.VideoTypeService;

@Controller
public class HomeController {
	
	private final static String[] FACECHARS = {"(ง •̀_•́)ง ","(•̀ᴗ•́)و ̑̑ ", "ヽ(•̀ω•́ )ゝ", 
			"(,,• ₃ •,,)", "(｡˘•ε•˘｡)", " (=ﾟωﾟ)ﾉ", "(╯‵□′)╯︵┻━┻", "ㄟ( ▔, ▔ )ㄏ", "(*´Д｀*) ",
			"(°□°；) ", "∑(っ °Д °;)っ"};
	
	@Autowired
	private VideoTypeService typeSerivce;
	
	/**
	 * 显示主页
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/", "/search"}, method=RequestMethod.GET)
	public ModelAndView mainPage(Model model){
		/*model.addAttribute("types", typeSerivce.list(new Integer(1)));
		return new ModelAndView("index");*/
		return new ModelAndView("redirect:/anime/focus");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/admin", method=RequestMethod.GET)
	public ModelAndView adminPage(Model model){
		return new ModelAndView("/admin/index");
	}
	
	/**
	 * 显示信息
	 * @return
	 */
	@RequestMapping(value="/common/message", method=RequestMethod.GET)
	public ModelAndView message(RedirectAttributes ra, Model model) {
		if(model.asMap().size() == 0) {
			Random random = new Random();
			model.addAttribute("success", FACECHARS[random.nextInt(FACECHARS.length)]);
		}
		model.addAllAttributes(ra.getFlashAttributes());
		return new ModelAndView("common_message");
	}
	
}
