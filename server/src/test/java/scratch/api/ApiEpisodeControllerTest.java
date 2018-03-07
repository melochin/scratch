package scratch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.test.ContextTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

@Transactional
public class ApiEpisodeControllerTest extends ContextTest {

	private AnimeEpisode episode;


	{
		episode = new AnimeEpisode();
		episode.setAnime(new Anime(new Long(1)));
		episode.setHostId(new Long(1));
		episode.setUrl("www.test.com");
		episode.setNumber("test");
	}


	@Rollback
	@Test
	public void getAnimes() throws Exception {
		mvc.perform(get("/api/admin/animes")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void list() throws Exception {
		mvc.perform(get("/api/admin/episodes")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void save() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(episode);

		mvc.perform(post("/api/admin/episodes")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void modify() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(episode);

		mvc.perform(put("/api/admin/episodes")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.with(getAdmin()).with(csrf().asHeader()))
				.andExpect(status().isOk());

	}

	@Rollback
	@Test
	public void remove() throws Exception {

		mvc.perform(delete("/api/admin/episodes/{episodeId}", "1")
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

}