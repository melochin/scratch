package scratch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import scratch.model.entity.AnimeComment;
import scratch.test.ContextTest;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAnimeCommentControllerTest extends ContextTest {

	@Test
	public void add() throws Exception {

		AnimeComment animeComment =
				new AnimeComment(new Long(1), new Long(1), "lala");

		String json = new ObjectMapper().writeValueAsString(animeComment);
		mvc.perform(post("/api/anime/comments")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}
}