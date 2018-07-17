package scratch.service.anime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import scratch.model.RedisKey;
import scratch.model.entity.AnimeComment;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimeCommentService {

	@Autowired
	private RedisTemplate redisTemplate;

	private RedisList<AnimeComment> comments(String animeId) {
		return new DefaultRedisList<AnimeComment>(RedisKey.animeCommnets(animeId), redisTemplate);
	}

	private RedisList<AnimeComment> comments(String animeId, String episodeId) {
		return new DefaultRedisList<AnimeComment>(RedisKey.animeCommnets(animeId, episodeId), redisTemplate);
	}

	public void add(AnimeComment animeComment) {

		Long animeId = animeComment.getAnimeId();
		Long episodeId = animeComment.getEpisodeId();

		Assert.notNull(animeId, "animeId is null");

		if (episodeId == null) {
			comments(animeId.toString()).add(animeComment);
		} else {
			comments(animeId.toString(), episodeId.toString()).add(animeComment);
		}

	}

	public void delete(AnimeComment animeComment) {
		Long animeId = animeComment.getAnimeId();
		Long episodeId = animeComment.getEpisodeId();

		Assert.notNull(animeId, "animeId is null");

		if (episodeId == null) {
			comments(animeId.toString()).remove(animeComment);
		} else {
			comments(animeId.toString(), episodeId.toString()).remove(animeComment);
		}
	}

	public List<AnimeComment> list(Long animeId) {

		Assert.notNull(animeId, "animeId is null");

		return comments(animeId.toString())
				.stream().collect(Collectors.toList());
	}

	public List<AnimeComment> list(Long animeId, Long episodeId) {

		Assert.notNull(animeId, "animeId is null");
		Assert.notNull(episodeId, "episodeId is null");

		return comments(animeId.toString(), episodeId.toString())
				.stream().collect(Collectors.toList());
	}

}
