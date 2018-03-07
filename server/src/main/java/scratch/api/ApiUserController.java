package scratch.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import scratch.exception.NotFoundException;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.web.JsonResult;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiUserController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object list(@RequestParam(value="page", required = false) Integer page) {
		if(page != null) {
			return userService.list(page, 10);
		}
		return userService.list();
	}

	@PostMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public User save(@RequestBody User user) {
		if(StringUtils.isEmpty(user.getPassword())) {
			user.setPassword("123456");
		}
		try {
			userService.save(user);
		} catch (MailException e) {
			e.printStackTrace();
		}
		return user;
	}

	@PutMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public User modify(@RequestBody User user) {
		userService.modify(user);
		return userService.getById(user.getUserId());
	}

	@PutMapping(value="/api/admin/users/reset/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public User resetPassword(@PathVariable Long userId) {
		User user = userService.getById(userId);
		if(user == null) throw new NotFoundException();
		user.setPassword("123456");
		userService.modify(user);
		return userService.getById(userId);
	}

	@DeleteMapping("/api/admin/users/{userId}")
	public void delete(@PathVariable(value = "userId", required = true) Long userId) {
		userService.deleteById(userId);
		return;
	}

	/**
	 * 校验username是否重名
	 * @param username
	 * @return validate true : 校验通过，不重名
	 */
	@RequestMapping(path="/api/validate/username", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	JsonResult existUser(@RequestParam("username") String username) {
		JsonResult result = new JsonResult();
		boolean validate = !userService.isExistByUsername(username);
		result.setValidate(validate);
		return result;
	}

	@GetMapping("/api/islogin")
	public @ResponseBody Map isLogin(
			@AuthenticationPrincipal UserAdapter userAdapter) {
		boolean isLogin = false;
		if(userAdapter != null) {
			isLogin = true;
		}
		Map map = new HashMap();
		map.put("isLogin", isLogin);
		map.put("userId", userAdapter.getUserId());
		return map;
	}

}
