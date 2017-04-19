package scratch.controller;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import scratch.support.ModelSupport;


@SessionAttributes({"reset_key", "reset_userId"})	//多个request请求中需要使用
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@RequestMapping(value="/user/update/pwd", method=RequestMethod.POST)
	public void updatePassword() { }
	
	/**
	 * 显示密码重置页面
	 * @return
	 */
	@RequestMapping(value="/user/reset", method=RequestMethod.GET)
	public String resetPasswordForm() {
		return "user_reset";
	}
	
	/**
	 * 发送密码重置邮箱
	 * PRG模式
	 * @param username
	 * @param email
	 * @param ra
	 * @return
	 */
	@RequestMapping(value="/user/resetmail", method=RequestMethod.POST)
	public String resetPasswordEmail(@RequestParam("username") String username, 
			@RequestParam("email") String email, RedirectAttributes ra) {
		//核对用户的邮箱信息
		User user = userService.get(username, email);
		if(user == null){
			ModelSupport.setError(ra, "用户的邮箱信息错误");
			return "redirect:/user/reset";
		}
		//发送邮件
		try {
			userService.sendRestMail(user);
			ModelSupport.setSuccess(ra, "重置链接已经发送到邮箱，链接有效期为10分钟。");
		} catch (MailException | MessagingException e) {
			e.printStackTrace();
			ModelSupport.setError(ra, "发送邮件失败");
			return "redirect:/common/message";
		}
		return "redirect:/common/message";
	}
	
	/**
	 * 根据KEY提供重设密码页面
	 * @param key
	 * @param userId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/user/resetpwd", method=RequestMethod.GET)
	public String resetPasswordForm(@RequestParam("key") String key, 
			@RequestParam("user") Long userId, Model model) {
		
		//判断缓存中是否存在值，若不存在直接返回错误页面
		if(!redisTemplate.hasKey(key)) {
			model.addAttribute("error", "无效页面");
			return "common_message";
		} else {
			String value = redisTemplate.opsForValue().get(key);
			if(value == null || value.isEmpty() || !value.equals(userId.toString())) {
				model.addAttribute("error", "无效页面");
				return "common_message";	
			}
		}
		
		if(!userService.decodeReset(key, userId)) {
			model.addAttribute("error", "无效页面");
			return "common_message";
		}
		
		//添加到Session中
		model.addAttribute("reset_userId", userId);	
		model.addAttribute("reset_key", key);
		
		return "user_reset_pwd";
	}
	
	/**
	 * 密码重置
	 * @param password
	 * @param userId
	 * @param ra
	 * @param status
	 * @return
	 */
	@RequestMapping(value="/user/resetpwd", method=RequestMethod.POST)
	public String resetPassword(@RequestParam("password") String password,
			@ModelAttribute("reset_userId") Long userId, 
			@ModelAttribute("reset_key") String key,
			RedirectAttributes ra, SessionStatus status) {
		
		User u = new User();
		u.setPassword(password);
		
		userService.update(u, userId);
		status.setComplete();
		
		//密码修改成功，移除缓存中的key，该key作废
		redisTemplate.delete(key);

		ModelSupport.setSuccess(ra, "密码重置成功");
		return "redirect:/common/message";
	}
	
}
