package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.exception.PrivilegeException;
import scratch.model.ohter.UserAdapter;
import scratch.service.anime.AnimeFocusService;

@RestController
public class ApiAnimeFocusController {

	@Autowired
	private AnimeFocusService service;

	/**
	 * 关注影视
	 *
	 * @param animeId
	 * @param userAdapter
	 */
	@GetMapping("/api/user/animes/{animeId}/focus")
	public void add(@PathVariable("animeId") Long animeId,
					  @AuthenticationPrincipal UserAdapter userAdapter) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);
		service.save(animeId, userAdapter.getUserId());
	}

	/**
	 * 取消关注影视
	 *
	 * @param animeId
	 * @param userAdapter
	 */
	@GetMapping("/api/user/animes/{animeId}/unfocus")
	public void delete(@PathVariable("animeId") Long animeId,
						 @AuthenticationPrincipal UserAdapter userAdapter) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);
		service.delete(animeId, userAdapter.getUserId());
	}

}
