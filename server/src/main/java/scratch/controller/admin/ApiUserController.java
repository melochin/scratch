package scratch.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import scratch.exception.NotFoundException;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;
import scratch.service.TokenService;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.web.JsonResult;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiUserController {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@PostMapping("/api/authen/admin")
	public JsonResult authen(@RequestParam("username") String username,
							 @RequestParam("password") String password) {
		try {

			Authentication authentication = userService.authen(username, password);
			UserAdapter userAdapter = (UserAdapter)authentication.getPrincipal();
			if(userAdapter.getRole() != 1) {
				return new JsonResult().setError("无效账号");
			}

			return new JsonResult().setAttribute("token",
					tokenService.sign(userAdapter.getUserId()));

		} catch (Exception e) {
			return new JsonResult().setError("无效账号");
		}
	}

	@GetMapping("/api/token")
	public String token(@AuthenticationPrincipal UserAdapter userAdapter) {
		return tokenService.sign(userAdapter.getUserId());
	}

	@GetMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object list(@RequestParam(value="page", required = false) Integer page) {
//		if(page != null) {
//			return userService.list(page, 10);
//		}
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
	public void delete(@PathVariable(value = "userId") Long userId) {
		userService.deleteById(userId);
	}

	/**
	 * 校验username是否重名
	 * @param username
	 * @return validate true : 校验通过，不重名
	 */
	@GetMapping(path = "/api/userid/{userid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	JsonResult existUser(@PathVariable("userid") long userId) {
		return new JsonResult().setAttribute("user", userService.getById(userId));
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
