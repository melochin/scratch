package scratch.controller.user;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.SessionContext;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;
import scratch.service.UserService;
import scratch.support.FileUtils;
import scratch.support.web.spring.ModelUtils;
import scratch.support.web.spring.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/** 在session中暂时存放request header referer */
@SessionAttributes("referer")
@RequestMapping("/user")
@Controller
public class LoginController {

	public static final String WEB_INF_RESOURCE_PIC_HOME = "/WEB-INF/resource/pic/home";
	private static Logger log = Logger.getLogger(LoginController.class);

	private final static String DEFAULT_REDIRECT_URL = "/";

	@Autowired
	private UserService service;

	/**
	 * 登录界面
	 * @see LoginController#login(User, String, SessionStatus)
	 * @return
	 */
	@GetMapping("/login")
	public String loginForm(@RequestHeader(name="referer", required=false, defaultValue="/") String referer,
							Model model, HttpServletRequest request) {
		//判断referer是不是含有/user，防止/user路径重定向/user路径
		if(!StringUtils.isEmpty(referer) && referer.indexOf("/user") > 0) {
			referer = DEFAULT_REDIRECT_URL;
		}

		// session attribute中存入referer,便于登录成功时重定向
		model.addAttribute("referer", referer);
		String path = request.getServletContext().getRealPath(WEB_INF_RESOURCE_PIC_HOME);
		String backgroundPic = FileUtils.getRandomFile(path);
		if(backgroundPic != null) {
			model.addAttribute("backgroundPic", backgroundPic);
		}
		return "/user/login";
	}

	/**
	 * 登录操作
	 * @param user
	 * @param referer
	 * @param status
	 * @return
	 */
	@PostMapping("/login")
	public String login(User user,
			@ModelAttribute("referer") String referer,
			SessionStatus status) {
		String url = DEFAULT_REDIRECT_URL;
		// 账号校验
		Authentication authentication = service.authen(user.getUsername(), user.getPassword());
		// 存入session
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SessionUtils.setAttribute(SessionContext.USER, ((UserAdapter)authentication.getPrincipal()));
		// get redirect
		if(!StringUtils.isEmpty(referer)) {
			url = referer;
			status.setComplete();
		}
		return "redirect:" + url;
	}


	/**
	 * 账号登出
	 * 注意：必须处于登录状态，才能调用登出
	 * @param ra
	 * @return
	 */
	@GetMapping("/logout")
	public String logout(RedirectAttributes ra) {
		SessionUtils.removeAttribute(SessionContext.USER);
		SecurityContextHolder.getContext().setAuthentication(null);
		ModelUtils.setSuccess(ra, "登出成功");
		return "redirect:/user/login";
	}
	
}
