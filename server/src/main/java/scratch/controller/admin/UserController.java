package scratch.controller.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.entity.User;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.service.PageBean;
import scratch.support.web.JsonResult;

@Controller("AdminUserController")
@RequestMapping("/admin/user")
public class UserController {
	
	private static final int PAGE_USER_SIZE = 10;
	
	@Autowired
	private UserService userService;
	
	@ModelAttribute
	public void addModel(Model model) {
		model.addAttribute("module", "user");
	}
	
	/**
	 * 显示用户信息
	 * 提供后台分页功能
	 * @param page
	 * @return
	 */
	@RequestMapping("")
	public String list(@RequestParam(value="p", defaultValue="1") int page, Model model) {
		PageBean<User> userList = userService.list(page, PAGE_USER_SIZE);
		model.addAttribute("userList", userList);
		return "/admin/user/index";
	}
	
	/**
	 * 根据用户ID查找用户信息
	 * 返回JSON
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{userId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult getUserInfo(@PathVariable("userId") Long id) {
		JsonResult result = new JsonResult();
		User user = userService.getById(id);
		if(user != null) {
			result.put("user", user);
		} else {
			result.setError("用户不存在");
		}
		return result;
	}
	
	@RequestMapping(value="/validate", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult validateUser(@RequestParam("username") String username) {
		JsonResult result = new JsonResult();
		boolean validate = false;
		validate = !userService.isExistByUsername(username);
		result.setValidate(validate);
		return result;
	}
	

	/**
	 * 
	 * 新增用户
	 * @param user
	 * @param referer
	 * @return
	 * @throws MailException
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public ModelAndView save(@Valid User user, 
			@RequestHeader(value="referer", required=false) String referer) throws MailException {
		ModelAndView view = new ModelAndView("redirect:/amin/user/index");
		userService.save(user);
		if(!StringUtils.isEmpty(referer)) {
			view = new ModelAndView("redirect:" + referer);
		}
		return view;
	}
	
	/**
	 * 
	 * 更新用户
	 * @param user
	 * @param status
	 * @return
	 */
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public ModelAndView update(User user, @RequestParam(required=false)String status) {
		if(status == null) {
			status = "0";
		}
		user.setStatus(status);
		userService.modify(user);
		return new ModelAndView("redirect:/admin/user");
	}
	
	/**
	 * 删除用户
	 * @param userId
	 * @param referer
	 * @return
	 */
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ModelAndView delete(@RequestParam("userId") Long userId, @RequestHeader("referer") String referer) {
		ModelAndView view = new ModelAndView("redirect:/admin/user/index");
		userService.deleteById(userId);
		if(!StringUtils.isEmpty(referer)) {
			view.setViewName("redirect:" + referer);
		}
		return view;
	}
	
}