package scratch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.service.anime.AnimeService;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class ApiAnimeControllerTest extends DocumentContextTest {

	private FieldDescriptor[] animeFields;

	@Autowired
	private AnimeService animeService;

	@Before
	public void setUp() {
		this.animeFields = new FieldDescriptor[]{
				fieldWithPath("id").description("ID"),
				fieldWithPath("name").description("名称"),
				fieldWithPath("description").description("描述").optional(),
				fieldWithPath("publishMonth").description("发布月份").optional(),
				fieldWithPath("finished").description("完结状态"),
				fieldWithPath("episodeNo").description("集号"),
				fieldWithPath("type").description("类型").optional(),
				fieldWithPath("pic").description("封面").optional(),
				subsectionWithPath("aliass").description("别名")
		};
	}

	@Test
	public void list() throws Exception {
		this.mvc.perform(get("/api/admin/animes").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("animes-list",
						responseFields(fieldWithPath("[]").description("animes"))
								.andWithPrefix("[].", this.animeFields)
						)
				);
	}

	@Rollback
	@Transactional
	@Test
	public void save() throws Exception {

		Anime anime = new Anime();
		anime.setName("test anime name");
		anime.setType("01");
		anime.setPic("pic.png");
		anime.setDescription("desc");
		anime.setFinished(false);
		anime.setPublishMonth(new Date());
		anime.setAliass(Arrays.asList(new AnimeAlias(new Long(1), new Long(1), "alias")));

		String json = new ObjectMapper().writeValueAsString(anime);

		this.mvc.perform(post("/api/admin/animes")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isOk())
				.andDo(document("animes-save",
						requestFields(this.animeFields)));
	}

	@Rollback
	@Transactional
	@Test
	public void modify() throws Exception {

		Anime anime = animeService.findById(new Long(195));
		anime.setFinished(false);
		anime.setPublishMonth(new Date());
		String json = new ObjectMapper().writeValueAsString(anime);

		this.mvc.perform(
				put("/api/admin/animes")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json)
		)
				.andExpect(status().isOk())
				.andDo(document("animes-modify", requestFields(this.animeFields)));
	}

	@Rollback
	@Transactional
	@Test
	public void remove() throws Exception {
		Long animeId = new Long(195);
		this.mvc.perform(
				RestDocumentationRequestBuilders
						.delete("/api/admin/animes/{animeId}", animeId)
		)
				.andExpect(status().isOk())
				.andDo(document("animes-delete",
						pathParameters(parameterWithName("animeId").description("id of anime"))));
	}

}