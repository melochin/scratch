package scratch.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.context.SessionContext;
import scratch.model.entity.User;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.web.JsonResult;
import scratch.support.web.spring.ModelUtils;
import scratch.support.web.spring.SessionUtils;

@Controller
@SessionAttributes("user")
@RequestMapping("/user")
public class RegisterController {
	
	@Autowired
	private UserService service;
	
	@ModelAttribute
	public void addUser(Model model) {
		if(!model.containsAttribute("user")) {
			model.addAttribute(new User());
		}
	}
	
	/**
	 * 显示用户注册表单
	 * @return
	 */
	@RequestMapping(path="/register", method=RequestMethod.GET)
	public String registerForm(){
		return "/user/register";
	}
	
	/**
	 * 处理用户注册表单
	 * @param user
	 * @param result
	 * @param ra
	 * @return
	 */
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result, 
			RedirectAttributes ra, SessionStatus status) {
		//校验用户信息
		if(result.hasErrors()) {
			ModelUtils.setError(ra, result);
			return "redirect:/user/register";
		}
		try{
			service.save(user);
			ModelUtils.setSuccess(ra, "注册成功，将会发送一份邮件给您，点击邮件里的链接完成账号验证，否则账号无法使用");
			status.setComplete();
		} catch(MailException e) {
			ModelUtils.setSuccess(ra, "账号注册成功:" + e.getMessage());
			status.setComplete();
		} catch(Exception e) {
			ModelUtils.setError(ra, e.getMessage());
			return "redirect:/user/register";
		}
		return "redirect:/user/login";
	}
	
	/**
	 * 发送邮箱校验
	 * @param session
	 * @param model
	 * @return
	 * @throws MailException
	 * @throws MessagingException
	 */
	@GetMapping(path="/register/sendMail")
	public String sendMail(@SessionAttribute(SessionContext.USER) User user, 
			RedirectAttributes ra) throws MailException, MessagingException {
		
		if(user == null) {
			throw new RuntimeException();
		}
		
		user = service.getById(user.getUserId());
		
		if(!"1".equals(user.getStatus())) {
			service.sendActiveMail(user);
			ModelUtils.setSuccess(ra, "邮件已经发送");
			return "redirect:/user/info";
		} 
		
		ModelUtils.setError(ra, "该用户账号已经激活，无需发送邮件");
		return "redirect:/user/info";
	}
	
	/**
	 * 账号激活链接
	 * @param userId
	 * @param actiCode
	 * @param ra
	 * @return
	 */
	@RequestMapping(path="/register/activiti/{userId}/{actiCode}", method=RequestMethod.GET)
	public String activiti(@PathVariable("userId") Long userId, 
			@PathVariable("actiCode") String actiCode, RedirectAttributes ra) {

		int result = service.activi(userId, actiCode);
		switch (result) {
			case -1:
				throw new RuntimeException();
			case 1 :
				ModelUtils.setSuccess(ra, "账号激活成功");
				User user = service.getById(userId);
				SessionUtils.setAttribute(SessionContext.USER, user);
				break;
			default:
				ModelUtils.setError(ra, "账号激活失败");
				break;
		}
		
		return "redirect:/user/info";
	}
	
	/**
	 * 校验username是否重名
	 * @param username
	 * @return validate true : 校验通过，不重名
	 */
	@RequestMapping(path="/api/validate/username", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult existUser(@RequestParam("username") String username) {
		JsonResult result = new JsonResult();
		boolean validate = !service.isExistByUsername(username);
		result.setValidate(validate);
		return result;
	}

}
