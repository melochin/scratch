package scratch.controller;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.exception.MailException;
import scratch.model.User;
import scratch.service.UserService;

@Controller
@SessionAttributes({"key", "userId"})
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/user/update/pwd", method=RequestMethod.POST)
	public void updatePassword() {
		
	}
	
	/**
	 * 密码重置：填写邮箱信息表单
	 * @return
	 */
	@RequestMapping(value="/user/reset", method=RequestMethod.GET)
	public String resetPasswordForm() {
		//显示填写表单
		return "user_reset";
	}
	
	/**
	 * 密码重置：邮件发送重置链接请求
	 * @param username
	 * @param email
	 * @param ra
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/user/resetmail", method=RequestMethod.POST)
	public String resetPasswordEmail(@RequestParam("username") String username, 
			@RequestParam("email") String email, RedirectAttributes ra, Model model) {
		//判断邮件是否属于该账号
		User user = userService.get(username, email);
		if(user == null){
			//显示错误
			ra.addAttribute("error", "输入的邮箱错误");
			return "redirect:/user/reset";
		}
		//成立则发送邮件
		try {
			userService.sendRestMail(user);
		} catch (MailException | MessagingException e) {
			//显示错误
			e.printStackTrace();
			model.addAttribute("error", "邮件发送失败");
			return "common_message";
		}
		model.addAttribute("error", "邮件发送成功");
		return "common_message";
	}
	
	/**
	 * 密码重置：核对重置URL有效性，返回密码填写表单
	 * @param key
	 */
	@RequestMapping(value="/user/resetpwd", method=RequestMethod.GET)
	public String resetPasswordForm(@RequestParam("key") String key, 
			@RequestParam("user") Long userId, Model model) {
		//校验KEY
		if(!userService.decodeReset(key, userId)) {
			model.addAttribute("error", "无效链接");
			return "common_message";
		}
		//返回密码重置页面
		model.addAttribute("userId", userId);
		return "user_reset_pwd";
	}
	
	/**
	 * 密码重置：密码更新请求
	 * @param password
	 * @param rePassword
	 * @param key			KEY应该用缓存管理，KEY使用完毕之后，就应该抛弃缓存
	 * @param userId
	 * @param model
	 * @param ra
	 * @return
	 */
	@RequestMapping(value="/user/resetpwd", method=RequestMethod.POST)
	public String resetPassword(@RequestParam("password") String password,
			@RequestParam("repassword") String rePassword,
			@ModelAttribute("userId") Long userId, 
			Model model, RedirectAttributes ra, SessionStatus status) {
		
		if(!password.equals(rePassword)) {
			ra.addAttribute("error", "两次密码不一致");
			return "redirect:/user/resetpwd";
		}
		
		User u = new User();
		u.setPassword(password);
		userService.update(u, userId);
		
		status.setComplete();
		model.addAttribute("success", "密码更新成功");
		return "common_message";
	}
	
}
