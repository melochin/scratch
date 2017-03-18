package scratch.controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.User;
import scratch.service.UserSerivce;
import scratch.support.RedirectAttrSupport;

@Controller
@SessionAttributes("user")
@RequestMapping("/user")
public class RegisterController {
	
	@Autowired
	private UserSerivce service;
	
	@ModelAttribute
	public void addUser(Model model) {
		if(!model.containsAttribute("user")) {
			model.addAttribute(new User());
		}
	}
	
	/**
	 * 用户注册表单
	 * @return
	 */
	@RequestMapping(path="/register", method=RequestMethod.GET)
	public String registerForm(){
		return "user_register";
	}
	
	/**
	 * 用户注册处理
	 * @param user
	 * @param result
	 * @param rePassword
	 * @param ra
	 * @return
	 */
	@RequestMapping(path="/register", method=RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result, 
			@RequestParam String rePassword, RedirectAttributes ra) {
		//校验信息
		if(result.hasErrors()) {
			RedirectAttrSupport.setError(ra, result);
			return "redirect:/user/register";
		}
		if(!user.getPassword().equals(rePassword)) {
			ra.addFlashAttribute("error", "两次输入密码不一致");
			return "redirect:/user/register";
		}
		//新增用户
		try{
			service.add(user);
		} catch(Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/user/register";
		}
		ra.addFlashAttribute("success", "注册成功，将会发送一份邮件给您，点击邮件里的链接完成账号验证，否则账号无法使用");
		return "redirect:/common/message";
	}
	
	@RequestMapping(path="/register/sendMail")
	public String sendMail(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if(user == null) {
			model.addAttribute("error", "无效链接");
			return "common_message";
		}
		if(!"1".equals(user.getStatus())) {
			service.sendActiviMail(user);
			model.addAttribute("success", "邮件已经发送");
			return "common_message";
		} 
		model.addAttribute("error", "该用户账号已经激活，无需发送邮件");
		return "common_message";
	}
	
	
	/**
	 * 用户注册激活
	 * @param actiCode
	 * @param request
	 * @return
	 */
	@RequestMapping(path="/register/activiti/{actiCode}", method=RequestMethod.GET)
	public String activiti(@PathVariable("actiCode") String actiCode, Model model) {
		if(!service.activi(actiCode)) {
			model.addAttribute("error", "您所访问的页面不存在");
			return "common_message";
		} else {
			model.addAttribute("success", "账号激活成功");
			return "common_message";
		}
	}
	
	@RequestMapping(path="/register/user", method=RequestMethod.POST)
	public @ResponseBody String existUser(@RequestParam("username") String username) {
		User user = new User();
		user.setUsername(username);
		return service.isExist(user) ? "账号存在" : null;
	}

}
