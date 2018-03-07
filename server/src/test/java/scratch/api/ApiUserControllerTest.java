package scratch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.User;
import scratch.test.ContextTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

@Transactional
public class ApiUserControllerTest extends ContextTest {

	private User user = new User();

	private ObjectMapper mapper = new ObjectMapper();

	{
		user.setUsername("testtest");
		user.setPassword("testtest");
		user.setStatus("1");
		user.setRole(1);
		user.setUserId(new Long(35));
	}

	@Rollback
	@Test
	public void list() throws Exception {
		mvc.perform(get("/api/admin/users")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void save() throws Exception {
		String json = mapper.writeValueAsString(user);
		mvc.perform(post("/api/admin/users")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void modify() throws Exception {
		String json = mapper.writeValueAsString(user);
		mvc.perform(put("/api/admin/users")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin()).with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void remove() throws Exception {
		mvc.perform(delete("/api/admin/users/{userId}", "1")
				.with(getAdmin()).with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

}