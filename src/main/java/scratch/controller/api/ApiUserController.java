package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.User;
import scratch.service.UserService;
import scratch.support.service.MailException;
import scratch.support.web.JsonResult;

import java.util.List;

@RestController
public class ApiUserController {

	@Autowired
	private UserService userService;

	@GetMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> list() {
		return userService.list();
	}

	@PostMapping(value = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public User save(@RequestBody User user) {
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


}
