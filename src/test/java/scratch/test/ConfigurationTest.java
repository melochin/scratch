package scratch.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import scratch.model.entity.User;
import scratch.service.UserService;


public class ConfigurationTest extends ContextTest{

	private MockMvc mockMvc;
	
	@Autowired
	private UserService userService;
	
	/**
	 * load  Spring  MVC configuration through the  TestContext framework, 
	 * which loads the Spring configuration and 
	 * injects a WebApplicationContext into the test to use to build a MockMvcinstance
	 */
	@Before
	public void setup() {
		//MockMvc��ע��WebApplicationContext
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
/*	@Test
	public void test() throws Exception {
		//perform return ResultActions
		//resultActions.andExpect(ResultMatcher matcher); �Խ�����������ж�
		//MockMvcResultMatchers.*provides a number of expectations
		MvcResult result = mockMvc.perform(get("/user/login"))
			.andDo(print())		//��ӡ���к����Ϣ
			.andDo(log())		//���}�����H��ʹ�õ���apache��log�����Nlog�ļ�ӛ������e��
			.andExpect(status().isOk())
			.andExpect(handler().methodName("loginForm"))
			.andExpect(view().name("/user/login"))
			.andReturn();
	}
	
	@Test
	public void testUser() throws Exception {
		mockMvc.perform(post("/user/register/user").param("username", "hejianok"))
				.andExpect(content().string("�˺Ŵ���"));
	}
	
	@Test
	public void testBean() {
		User u = new User();
		u.setUsername("hejianok");
		BeanWrapperImpl bean = new BeanWrapperImpl(u);
		bean.setPropertyValue("username", "rory");
		bean.getPropertyValue("username");
		Assert.assertEquals(bean.getPropertyValue("username"), "rory");
	}
	
	@Test
	public void testFormatter() {
		DateTimeFormatter format = new DateTimeFormatterFactory().createDateTimeFormatter();
		System.out.println(format.print(new DateTime(), new Locale("cn")));
		System.out.println(format.print(new Date(), new Locale("en")));
	}*/
}
