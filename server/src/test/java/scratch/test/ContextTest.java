package scratch.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
//配置上下文环境
@ContextConfiguration({"file:src/main/webapp/WEB-INF/applicationContext.xml",
	"file:src/main/webapp/WEB-INF/spring-servlet.xml"})
public class ContextTest {

	@Autowired
	private Environment enviroment;
	
	@Autowired
	protected WebApplicationContext wac;
	
	protected MockMvc mvc;
	
	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(springSecurity())
				.build();
	}

	private User generateUser() {
		User user = new User();
		user.setUsername("consequent");
		user.setUserId(new Long(35));
		return user;
	}

	public RequestPostProcessor getUser() {
		User user = generateUser();
		user.setRole(0);
		return user(new UserAdapter(user));
	}

	public RequestPostProcessor getAdmin() {
		User user = generateUser();
		user.setRole(1);
		return user(new UserAdapter(user));
	}
}
