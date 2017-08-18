package scratch.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.context.SessionContext;
import scratch.model.entity.User;
import scratch.test.ContextTest;

public class UserControllerTest extends ContextTest{

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Test
	public void infoFormTest() throws Exception {
		mvc.perform(get("/user/info")
				.sessionAttr(SessionContext.USER, new User()))
		.andExpect(status().isOk())
		.andExpect(view().name("/user/info"));
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void resetPwdTest() throws Exception {
		// 重置密码申请表单
		mvc.perform(get("/user/reset/password/apply"))
		.andExpect(status().isOk())
		.andExpect(view().name("/user/reset"));
		
		// 重置密码邮件验证(生成验证链接)
		mvc.perform(post("/user/reset/pwd/mail")
				.param("username", "consequent")
				.param("email", "398299262@qq.com"))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/user/reset/password/apply"))
		.andExpect(flash().attributeExists("success"));
		
		// 验证链接身份验证
		String key = redisTemplate.opsForValue().get("reset_35");
		mvc.perform(get("/user/reset/pwd")
				.param("resetcode", key)
				.param("user", "35"))
		.andExpect(view().name("/user/reset-pwd"))
		.andExpect(model().attributeExists("reset_userId", "reset_code"));
		
		// 密码重置
		mvc.perform(post("/user/reset/pwd")
				.param("password", "123465")
				.sessionAttr("reset_userId", new Long(35))
				.sessionAttr("reset_code", key))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/user/login"))
		.andExpect(flash().attributeExists("success"));
	}

}
