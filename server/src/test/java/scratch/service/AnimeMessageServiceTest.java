package scratch.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.service.anime.AnimeMessageService;
import scratch.test.ContextTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class AnimeMessageServiceTest extends ContextTest {

	@Autowired
	AnimeMessageService service;

	@Test
	public void push() throws Exception {
		List animeEpisodes = new ArrayList();
		AnimeEpisode animeEpisode = new AnimeEpisode();
		animeEpisode.setHostId(new Long(1));
		animeEpisode.setNumber("1");
		animeEpisode.setId(new Long(1));
		animeEpisode.setScratchTime(new Date());
		animeEpisode.setSaveTime(new Date());
		animeEpisode.setPushTime(new Date());
		animeEpisodes.add(animeEpisode);
/*		ObjectMapper mapper = new ObjectMapper();
		byte[] body = mapper.writeValueAsBytes(animeEpisodes);

		JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, AnimeEpisode.class);
		try{
			List<AnimeEpisode> episodes = mapper.readValue(body, javaType);
			System.out.println(episodes);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		service.push(animeEpisodes);
	}

}