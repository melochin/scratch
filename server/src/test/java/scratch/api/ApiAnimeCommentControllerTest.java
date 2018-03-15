package scratch.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import scratch.model.entity.AnimeComment;
import scratch.test.ContextTest;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAnimeCommentControllerTest extends ContextTest {

	@Test
	public void addTest() throws Exception {

		AnimeComment animeComment =
				new AnimeComment(new Long(1), new Long(1), "lala");
		String json = new ObjectMapper().writeValueAsString(animeComment);
		mvc.perform(post("/api/anime/comments")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getUser())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteTest() throws Exception {

		AnimeComment animeComment =
				new AnimeComment(new Long(1), new Long(1), "lala");
		String json = new ObjectMapper().writeValueAsString(animeComment);

		mvc.perform(delete("/api/anime/comments")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getUser())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());


	}

}