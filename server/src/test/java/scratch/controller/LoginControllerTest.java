package scratch.controller;

import org.junit.Test;

import scratch.model.SessionContext;
import scratch.model.entity.User;
import scratch.test.ContextTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class LoginControllerTest extends ContextTest {

	@Test
	public void loginTest() throws Exception {
		// 登录表单测试
		mvc.perform(get("/user/login"))
			.andExpect(model().attributeExists("referer"))
			.andExpect(view().name("/user/login"));
		
		// 登录请求测试
		mvc.perform(post("/user/login")
				.param("username", "consequent")
				.param("password", "hejianok")
				.sessionAttr("referer", "/"))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/"));
		
	}
	
	@Test
	public void logoutTest() throws Exception {
		
		// 直接登出404错误
		mvc.perform(get("/user/logout"))
		.andExpect(status().is(404))
		.andExpect(view().name("/base/404"));
		
		// 存在用户的情况下登出
		User user = new User(new Long(35));
		mvc.perform(get("/user/logout")
				.sessionAttr(SessionContext.USER, user))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/"));
		
	}

}
