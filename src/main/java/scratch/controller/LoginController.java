package scratch.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.User;
import scratch.service.UserService;
import scratch.support.CookieSupport;
import scratch.support.GlobalSession;
import scratch.support.SessionSupport;

@Controller
@RequestMapping("/user")
public class LoginController {

	@Autowired
	private UserService service;
	
	/**
	 * 用户登录界面
	 * @return
	 */
	@RequestMapping(path="/login", method=RequestMethod.GET)
	public String loginForm() {
		return "user_login";
	}
	
	/**
	 *用户登录处理 
	 */
	@RequestMapping(path="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute("user") User user, @RequestParam(required=false) boolean remember, 
			RedirectAttributes ra, HttpSession session, HttpServletResponse response) {
		//校验账号密码
		User curUser= service.verify(user);
		if(curUser == null) {
			ra.addFlashAttribute(user)
			  .addFlashAttribute("error", "账号或密码错误");
			return "redirect:/user/login";
		}
		//若记住账号，生成Cookie返回给客户端
		if(remember) {
			CookieSupport.addUser(curUser);
		}
		//将账号信息放入session
		session.setAttribute(GlobalSession.USER, curUser);
		return "redirect:/";
	}
	
	/**
	 * 登出
	 * @param session
	 * @return
	 */
	@RequestMapping(path="/logout", method=RequestMethod.GET)
	public String logout(HttpSession session, Model model) {
		//session中存在用户，则移除信息顺利登出
		//如果不存在，则无需移除信息
		SessionSupport.removeUser();
		model.addAttribute("success", "登出成功");	
		return "common_message";
	}
	
	
}
