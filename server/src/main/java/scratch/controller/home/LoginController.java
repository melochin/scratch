package scratch.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import scratch.support.web.spring.ModelUtils;
import scratch.support.web.spring.SessionUtils;

import javax.servlet.http.HttpServletRequest;

@SessionAttributes("referer")
@Controller
@RequestMapping("/user")
public class LoginController extends HomeController{

	@Autowired
	private UserService userService;

	/**
	 * 登录页面
	 *
	 * 1. 未登录用户 才能进入登录页面
	 * 2. 已登录用户 自动跳转到用户中心
	 *
	 * @param referer
	 * @param user
	 * @param model
	 * @return
	 */
	@GetMapping("/login")
	public String loginForm(@RequestHeader(name="referer", required=false, defaultValue="/") String referer,
							@AuthenticationPrincipal UserAdapter user,
							Model model) {

		if(user != null) {
			return "redirect:" + USER_CENTER_URL;
		}

		model.addAttribute("referer", referer);
		addBackgroundPic(model);

		return "/user/login";
	}


	/**
	 * 登录操作
	 *
	 * @param user
	 * @param referer
	 * @param status
	 * @return
	 */
	@PostMapping("/login")
	public String login(User user,
						Model model,
						@ModelAttribute("host") String host,
						@ModelAttribute("referer") String referer,
						SessionStatus status) {

		String redirectUrl = USER_CENTER_URL;

		// 账号校验
		Authentication authentication = userService.authen(user.getUsername(), user.getPassword());
		// 存入session
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SessionUtils.setAttribute(SessionContext.USER, ((UserAdapter)authentication.getPrincipal()));

		// 设置跳转地址
		System.out.println("referer:" + referer);

		// 使用referer 作为 重定向地址的条件
		// 1. referer 非空 且不等于 "/"
		// 2. referer 的path 不能是 "/" 或者 ""
		if(!StringUtils.isEmpty(referer) && !referer.equals("/")) {
			if(referer.contains(host)) {
				String[] path = referer.split(host);
				if(path.length == 2) {
					if(!path[1].equals("/") && !path[1].equals("")) {
						redirectUrl = referer;
					}
				}
			}
		}

		status.setComplete();
		model.asMap().clear();

		return "redirect:" + redirectUrl;
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
		return "redirect:" + INDEX_URL;
	}

}
