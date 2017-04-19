package scratch.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.aspect.UserRole;
import scratch.model.User;
import scratch.service.UserService;
import scratch.support.CookieSupport;
import scratch.support.SessionSupport;

@Controller
@RequestMapping("/user")
public class LoginController {

	@Autowired
	private UserService service;
	
	/**
	 * 显示登录界面
	 * @return
	 */
	@RequestMapping(path="/login", method=RequestMethod.GET)
	public String loginForm() {
		return "user_login";
	}
	
	/**
	 * 处理登录
	 * @param user
	 * @param remember
	 * @param ra
	 * @return
	 */
	@RequestMapping(path="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute("user") User user, @RequestParam(required=false) boolean remember, 
			RedirectAttributes ra) {
		//校验用户身份
		User curUser= service.verify(user);
		if(curUser == null) {
			ra.addFlashAttribute(user)
			  .addFlashAttribute("error", "账号密码错误");
			return "redirect:/user/login";
		}
		if(remember) {
			CookieSupport.addUser(curUser);
		}
		SessionSupport.setUser(curUser);
		return "redirect:/";
	}
	
	/**
	 * 处理登出
	 * 注意：必须处于登录状态，才能调用登出
	 * @param session
	 * @return
	 */
	@UserRole(activi=false)
	@RequestMapping(path="/logout", method=RequestMethod.GET)
	public String logout(HttpSession session, Model model) {
		SessionSupport.removeUser();
		model.addAttribute("success", "用户登出成功");	
		return "common_message";
	}
	
	
}
