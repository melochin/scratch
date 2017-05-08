package scratch.controller.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.model.User;
import scratch.service.UserService;
import scratch.support.Result;
import scratch.support.service.MailException;
import scratch.support.service.PageBean;

@Controller("AdminUserController")
@RequestMapping("/admin/user")
public class UserController {
	
	private static final int PAGE_USER_SIZE = 10;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 显示用户信息
	 * 提供后台分页功能
	 * @param page
	 * @return
	 */
	@UserRole(Role.Admin)
	@RequestMapping("/index")
	public ModelAndView list(@RequestParam(value="p", defaultValue="1") int page) {
		ModelAndView model = new ModelAndView("admin_user");
		PageBean<User> userList = userService.findAll(page, PAGE_USER_SIZE);
		model.addObject("userList", userList);
		return model;
	}
	
	/**
	 * 根据用户ID查找用户信息
	 * 返回JSON
	 * @param id
	 * @return
	 */
	@UserRole(Role.Admin)
	@RequestMapping(value="/{userId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Result<User> getUserInfo(@PathVariable("userId") Long id) {
		Result<User> result = null;
		User user = userService.getById(id);
		if(user != null) {
			result = new Result<User>(user);
		} else {
			result = new Result<User>("用户不存在");
		}
		return result;
	}
	
	@UserRole(Role.Admin)
	@RequestMapping(value="/form/{userId}")
	public ModelAndView userForm(@PathVariable("userId") Long id, Model model) {
		User user = userService.getById(id);
		model.addAttribute("user", user);
		return new ModelAndView("/admin/user/edit");
	}
	
	/**
	 * 
	 * 新增用户
	 * @param user
	 * @param referer
	 * @return
	 * @throws MailException
	 */
	@UserRole(Role.Admin)
	@RequestMapping(value="save", method=RequestMethod.POST)
	public ModelAndView save(@Valid User user, 
			@RequestHeader(value="referer", required=false) String referer) throws MailException {
		ModelAndView view = new ModelAndView("redirect:/amin/user/index");
		userService.add(user);
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
	@UserRole(Role.Admin)
	@RequestMapping(value="update", method=RequestMethod.POST)
	public ModelAndView update(User user, @RequestParam(required=false)String status) {
		if(status == null) {
			status = "0";
		}
		user.setStatus(status);
		userService.update(user);
		return new ModelAndView("redirect:/admin/user/index");
	}
	
	/**
	 * 删除用户
	 * @param userId
	 * @param referer
	 * @return
	 */
	@UserRole(Role.Admin)
	@RequestMapping(value="delete", method=RequestMethod.GET)
	public ModelAndView delete(@RequestParam("userId") Long userId, @RequestHeader("referer") String referer) {
		ModelAndView view = new ModelAndView("redirect:/admin/user/index");
		userService.deleteById(userId);
		if(!StringUtils.isEmpty(referer)) {
			view.setViewName("redirect:" + referer);
		}
		return view;
	}
	
}