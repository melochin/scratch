package scratch.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.entity.User;
import scratch.test.ContextTest;

public class RegisterControllerTest extends ContextTest {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void registerTest() throws Exception {

		// 表单
		mvc.perform(get("/user/register"))
		.andExpect(status().isOk())
		.andExpect(view().name("/user/register"));
		
		// 注册
		mvc.perform(post("/user/register")
				.param("username", "registertest")
				.param("password", "registertest")
				.param("email", "398299262@qq.com"))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/user/login"))
		.andExpect(flash().attributeExists("success"));
		
		// 发送激活邮箱
		Long userId = new Long(43);
		User user = new User(userId);
		mvc.perform(get("/user/register/sendMail"))
				//.sessionAttr(SessionContext.USER, user))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/user/info"))
		.andExpect(flash().attributeExists("success"));
		
		// 激活链接
		String code = redisTemplate.opsForValue().get("active_" + Long.valueOf(userId));
		mvc.perform(get("/user/register/activiti/" + Long.valueOf(userId) + "/" + code ))
		.andExpect(status().is(302))
		.andExpect(view().name("redirect:/user/info"));
		
	}

}
