package scratch.service.anime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import scratch.model.RedisKey;
import scratch.model.entity.AnimeComment;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class AnimeCommentServiceTest extends ContextTest {

	@Autowired
	private AnimeCommentService commentService;

	@Autowired
	private RedisTemplate redisTemplate;

	private Long animeId = Long.valueOf(-1);

	private Long episodeId = Long.valueOf(-2);

	@Test
	public void add() throws Exception {

		AnimeComment animeComment = new AnimeComment(animeId, new Long(3), "test");
		AnimeComment episodeComments = new AnimeComment(animeId, episodeId, new Long(3), "test");

		commentService.add(animeComment);
		commentService.add(episodeComments);

		assertTrue(commentService.list(animeId).size() == 1);
		assertTrue(commentService.list(animeId, episodeId).size() == 1);

		clear(animeId.toString());
		clear(animeId.toString(), episodeId.toString());
	}

	@Test
	public void delete() throws Exception {

		AnimeComment animeComment = new AnimeComment(animeId, new Long(3), "test");
		AnimeComment episodeComments = new AnimeComment(animeId, episodeId, new Long(3), "test");

		commentService.add(animeComment);
		commentService.add(episodeComments);

		commentService.delete(animeComment);
		commentService.delete(episodeComments);

		assertTrue(commentService.list(animeId).size() == 0);
		assertTrue(commentService.list(animeId, episodeId).size() == 0);

		clear(animeId.toString());
		clear(animeId.toString(), episodeId.toString());

	}


	private void clear(String animeId) {
		redisTemplate.delete(RedisKey.animeCommnets(animeId));
	}

	private void clear(String animeId, String episodeId) {
		redisTemplate.delete(RedisKey.animeCommnets(animeId, episodeId));
	}

}