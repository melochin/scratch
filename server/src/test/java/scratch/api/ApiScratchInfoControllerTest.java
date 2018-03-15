package scratch.api;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.test.ContextTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

@Transactional
public class ApiScratchInfoControllerTest extends ContextTest {

	@Rollback
	@Test
	public void getRunMessage() throws Exception {
		mvc.perform(get("/api/admin/scratch/run/message")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void run() throws Exception {
		mvc.perform(get("/api/admin/scratch/run")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

}