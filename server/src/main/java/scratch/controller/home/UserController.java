package scratch.controller.home;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.exception.ValidationException;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.web.spring.ModelUtils;


@SessionAttributes({"reset_code", "reset_userId"})    //多个request请求中需要使用
@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 用户基本信息
	 * 需要存在user session，才能访问
	 * <p>
	 * model: user User
	 * view: "/user/info"
	 *
	 * @param userAdapter
	 * @param model
	 * @return
	 */
	@GetMapping("/info")
	public String userInfo(@AuthenticationPrincipal UserAdapter userAdapter,
						   Model model) {
		User user = userService.getById(userAdapter.getUserId());
		model.addAttribute("user", user);
		return "/user/info";
	}

	/**
	 * 等待开发
	 */
	@PostMapping("/update/pwd")
	public String updatePassword(@RequestParam("oldPass") String oldPass,
								 @RequestParam("newPass") String newPass,
								 @RequestParam("secondNewPass") String secondNewPass,
								 RedirectAttributes ra,
								 @AuthenticationPrincipal UserAdapter userAdapter) {

		if (newPass.isEmpty()) {
			throw new ValidationException("新密码不能为空");
		}

		if (!newPass.equals(secondNewPass)) {
			throw new ValidationException("新密码不一致，请重新输入");
		}

		userService.modifyPassword(userAdapter.getUsername(), oldPass, newPass);

		ModelUtils.setSuccess(ra, "更改成功");

		return "redirect:/user/info";
	}

	@PostMapping("/update/email")
	public String modifyEmail(@AuthenticationPrincipal UserAdapter user,
							  @RequestParam("email") String email,
							  @RequestHeader("referer") String referer,
							  RedirectAttributes ra) {
		try {
			userService.modifyEmail(user.getUserId(), email);
			ModelUtils.setSuccess(ra, "邮箱更改成功，并已发出认证邮件");
		} catch (Exception e) {
			ModelUtils.setError(ra, e.getMessage());
		}
		return "redirect:" + referer;
	}

	/**
	 * 密码重置申请页面（需要填写邮箱信息）
	 *
	 * @return
	 */
	@GetMapping("/reset/password/apply")
	public String resetPasswordApplyForm() {
		return "/user/reset";
	}

	/**
	 * 发送密码重置邮件(内含可以进行密码重置处理的链接)
	 * PRG模式
	 *
	 * @param username
	 * @param email
	 * @param ra
	 * @return
	 */
	@PostMapping("/reset/pwd/mail")
	public String resetPasswordEmail(@RequestParam("username") String username,
									 @RequestParam("email") String email, RedirectAttributes ra) {
		String redirectUrl = "redirect:/user/reset/password/apply";

		//核对用户的邮箱信息
		User user = userService.getByNameAndEmail(username, email);
		if (user == null) {
			ModelUtils.setError(ra, "用户的邮箱信息错误");
			return redirectUrl;
		}

		//发送邮件
		try {
			userService.sendRestMail(user);
			ModelUtils.setSuccess(ra, "重置链接已经发送到邮箱，链接有效期为10分钟。");
		} catch (MailException | MessagingException e) {
			e.printStackTrace();
			ModelUtils.setError(ra, "发送邮件失败");
		}

		return redirectUrl;
	}

	/**
	 * 显示密码重置页面（需校验resetcode和userid是否符合条件）
	 *
	 * @param key
	 * @param userId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/reset/pwd", method = RequestMethod.GET)
	public String resetPasswordForm(@RequestParam("resetcode") String resetCode,
									@RequestParam("user") Long userId, Model model) {

		if (!userService.canReset(userId, resetCode)) {
			throw new RuntimeException();
		}
		userService.getById(userId);

		//添加到Session中
		model.addAttribute("reset_userId", userId);
		model.addAttribute("reset_code", resetCode);

		return "/user/reset-pwd";
	}

	/**
	 * 密码重置处理（需要再次校验resetcode和userid，避免有人恶意发起POST请求）
	 *
	 * @param password
	 * @param userId
	 * @param ra
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/reset/pwd", method = RequestMethod.POST)
	public String resetPassword(@RequestParam("password") String password,
								@ModelAttribute("reset_userId") Long userId,
								@ModelAttribute("reset_code") String resetCode,
								RedirectAttributes ra, SessionStatus status, Model model) {

		if (!userService.canReset(userId, resetCode)) {
			throw new RuntimeException();
		}

		userService.resetPassword(userId, password);
		status.setComplete();

		ModelUtils.setSuccess(ra, "密码重置成功");

		return "redirect:/login";
	}

}
