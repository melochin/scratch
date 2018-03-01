package scratch.service.anime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scratch.model.entity.AnimeComment;
import scratch.service.RedisService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimeCommentService {

	private static final String ANIME_COMMENT = "AnimeComment";

	@Autowired
	private RedisService redisService;

	public void add(AnimeComment animeComment) {
		List<AnimeComment> animeCommentList =
				(List<AnimeComment>) redisService.hashGet(ANIME_COMMENT, animeComment.getAnimeId().toString());
		if(animeCommentList == null) {
			animeCommentList = new ArrayList<>();
		}
		animeCommentList.add(animeComment);
		redisService.hashPut(ANIME_COMMENT, animeComment.getAnimeId().toString(), animeCommentList);
	}

	public void delete(AnimeComment animeComment) {
		List<AnimeComment> animeCommentList =
				(List<AnimeComment>) redisService.hashGet(ANIME_COMMENT, animeComment.getAnimeId().toString());

		if(animeCommentList == null) return;
		animeCommentList.remove(animeComment);
		redisService.hashPut(ANIME_COMMENT, animeComment.getAnimeId().toString(), animeCommentList);
	}

	public List<AnimeComment> list(Long animeId) {
		return (List<AnimeComment>) redisService.hashGet(ANIME_COMMENT, animeId.toString());
	}

}
