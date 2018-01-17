package scratch.controller.api;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.test.ContextTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.junit.Assert.*;

@Transactional
public class ApiAnimeFocusControllerTest extends ContextTest {

	@Rollback
	@Test
	public void focus() throws Exception {
		mvc.perform(get("/api/user/animes/{animeId}/focus", 1)
				.with(getUser()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void unfocs() throws Exception {
		mvc.perform(get("/api/user/animes/{animeId}/unfocus", 1)
				.with(getUser()))
				.andExpect(status().isOk());
	}

}