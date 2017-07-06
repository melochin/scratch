package scratch.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
//配置上下文环境
@ContextConfiguration({"file:src/main/webapp/WEB-INF/applicationContext.xml",
	"file:src/main/webapp/WEB-INF/spring-servlet.xml"})
public class ContextTest {

	@Autowired
	protected WebApplicationContext wac;
	
	protected MockMvc mvc;
	
	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
				.alwaysExpect(status().isOk())
				.build();
	}
}
