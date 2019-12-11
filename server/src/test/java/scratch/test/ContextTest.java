package scratch.test;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import scratch.model.entity.User;
import scratch.model.ohter.UserAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

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

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	protected MockMvc mvc;

	protected AbstractMockMvcBuilder doMockMvc() {
		return MockMvcBuilders.webAppContextSetup(wac)
				.apply(springSecurity());
	}

	@Before
	public void setup() {
		this.mvc = doMockMvc().build();
/*		try {
			this.execSql();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	private User generateUser() {
		User user = new User();
		user.setUsername("consequent");
		user.setUserId(new Long(35));
		return user;
	}

	public Authentication getAuthentication() {
		User user = new User(new Long(1));
		UserAdapter adapter = new UserAdapter(user);
		return new UsernamePasswordAuthenticationToken(adapter, "test", adapter.getAuthorities());
	};

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

	private void execSql() throws IOException {
		Connection connection = sqlSessionFactory
				.openSession().getConnection();
		ScriptRunner runner = new ScriptRunner(connection);
		Reader reader = new InputStreamReader(new ClassPathResource("create_sql.sql").getInputStream());
		runner.runScript(reader);
	}
}
