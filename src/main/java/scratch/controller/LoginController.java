package scratch.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.aspect.UserRole;
import scratch.model.User;
import scratch.service.UserService;
import scratch.support.web.CookieSupport;
import scratch.support.web.SessionSupport;

/** 在session中暂时存放request header referer */
@SessionAttributes("referer")
@RequestMapping("/user")
@Controller
public class LoginController {
	
	private static Logger log = Logger.getLogger(LoginController.class);

	@Autowired
	private UserService service;
	
	/**
	 * 显示登录界面
	 * @return
	 */
	@RequestMapping(path="/login", method=RequestMethod.GET)
	public String loginForm(@RequestHeader(name="referer", required=false, defaultValue="/") String referer, 
			HttpServletRequest request, Model model) {
		//判断referer是不是自身路径，防止重复定向
		if(!StringUtils.isEmpty(referer)) {
			if(referer.indexOf("/user") > 0 || referer.indexOf("/common") > 0) {
				referer = "/";
			}
		}
		if(log.isDebugEnabled()) {
			log.debug("request referer:" + referer);
		}
		model.addAttribute("referer", referer);
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
	public String login(User user, 
			@ModelAttribute("referer") String referer,
			@RequestParam(required=false) boolean remember, 
			RedirectAttributes ra, SessionStatus status) {
		String url = "redirect:/";
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
		if(!StringUtils.isEmpty(referer)) {
			url = "redirect:" + referer;
			status.setComplete();
		}
		return url;
	}
	
	/**
	 * 处理登出
	 * 注意：必须处于登录状态，才能调用登出
	 * @param session
	 * @return
	 */
	@UserRole(activi=false)
	@RequestMapping(path="/logout", method=RequestMethod.GET)
	public String logout() {
		SessionSupport.removeUser();
		return "redirect:/";
	}
	
}
