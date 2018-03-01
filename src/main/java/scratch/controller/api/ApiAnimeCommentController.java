package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.AnimeComment;
import scratch.model.ohter.UserAdapter;
import scratch.service.anime.AnimeCommentService;
import scratch.support.web.JsonResult;

import java.util.Date;
import java.util.List;

@RestController("/api")
public class ApiAnimeCommentController {

	@Autowired
	private AnimeCommentService animeCommentService;

	@PostMapping(value="/anime/comments")
	public @ResponseBody JsonResult add(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@RequestBody AnimeComment animeComment) {
		animeComment.setUserId(userAdapter.getUserId());
		animeComment.setDate(new Date());
		animeComment.setUsername(userAdapter.getUsername());
		animeCommentService.add(animeComment);
		return new JsonResult().setSuccess(true);
	}

	@DeleteMapping("/anime/comments")
	public @ResponseBody JsonResult delete(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@RequestBody AnimeComment animeComment) {
		animeComment.setUserId(userAdapter.getUserId());
		animeCommentService.delete(animeComment);
		return new JsonResult().setSuccess(true);


	}

	@GetMapping("anime/comments/{animeId}")
	public @ResponseBody List<AnimeComment> list(@PathVariable("animeId") Long animeId) {
		return animeCommentService.list(animeId);
	}

}
