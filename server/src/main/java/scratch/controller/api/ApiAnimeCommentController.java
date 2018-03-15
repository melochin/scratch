package scratch.controller.api;

import scratch.exception.PrivilegeException;
import scratch.support.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.AnimeComment;
import scratch.model.ohter.UserAdapter;
import scratch.service.anime.AnimeCommentService;

import java.util.Date;
import java.util.List;

@RequestMapping("/api")
@RestController
public class ApiAnimeCommentController {

	@Autowired
	private AnimeCommentService animeCommentService;

	/**
	 * 功能：显示特定anime的评论
	 * method : get
	 * 参数：animeId
	 * 返回：List<Anime>
	 * 权限：无限制
	 *
	 * @param animeId
	 * @return
	 */
	@GetMapping("/anime/comments/{animeId}")
	public @ResponseBody List<AnimeComment> list(@PathVariable("animeId") Long animeId) {
		return animeCommentService.list(animeId);
	}

	/**
	 * 功能：新增评论
	 * method : post
	 * 参数：json AnimeComment
	 * 权限：用户级
	 *
	 * @param userAdapter	当前登录用户
	 * @param animeComment	评论
	 * @return
	 */
	@PostMapping(value="/anime/comments")
	public @ResponseBody JsonResult add(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@RequestBody AnimeComment animeComment) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);

		animeComment.setUserId(userAdapter.getUserId());
		animeComment.setDate(new Date());
		animeComment.setUsername(userAdapter.getUsername());
		animeCommentService.add(animeComment);
		return new JsonResult().setSuccess(true);
	}

	/**
	 * 功能：删除评论
	 * method : delete
	 * 参数：json AnimeComment
	 * 权限：用户级
	 *
	 * @param userAdapter	登录用户
	 * @param animeComment	评论
	 * @return
	 */
	@DeleteMapping("/anime/comments")
	public @ResponseBody JsonResult delete(
			@AuthenticationPrincipal UserAdapter userAdapter,
			@RequestBody AnimeComment animeComment) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);

		animeComment.setUserId(userAdapter.getUserId());
		animeCommentService.delete(animeComment);
		return new JsonResult().setSuccess(true);


	}

}
