package scratch.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import scratch.model.entity.Card;
import scratch.test.ContextTest;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiCardControllerTest extends ContextTest {


	@Test
	public void test() throws Exception {
		Card card = new Card("爱", "あい");
		String json = new ObjectMapper().writeValueAsString(card);
		// add
		mvc.perform(post("/api/cards")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
		// delete
		mvc.perform(delete("/api/cards")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin())
				.with(csrf().asHeader())).andExpect(status().isOk());
		// list
		mvc.perform(get("/api/stream/cards"))
				.andExpect(status().isOk());

	}

}