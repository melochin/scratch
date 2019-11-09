package scratch.controller.home;

import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.exception.PrivilegeException;
import scratch.model.SessionContext;
import scratch.model.ohter.UserAdapter;
import scratch.service.anime.AnimeFocusService;
import scratch.support.web.spring.SessionUtils;

@RestController
public class ApiAnimeFocusController {

	@Autowired
	private AnimeFocusService service;

	/**
	 * 关注影视
	 * @param animeId anime id
	 * @param userAdapter user info
	 */
	@PostMapping("/api/user/animes/{animeId}/focus")
	public void focus(@PathVariable("animeId") Long animeId,
					  @AuthenticationPrincipal UserAdapter userAdapter) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);
		service.save(animeId, userAdapter.getUserId());
	}

	/**
	 * 取消关注影视
	 *
	 * @param animeId
	 */
	@DeleteMapping("/api/user/animes/{animeId}/focus")
	public void unFocus(@PathVariable("animeId") Long animeId,
						@AuthenticationPrincipal UserAdapter userAdapter) {
		if(userAdapter == null) throw new PrivilegeException(PrivilegeException.NOLOGIN);
		service.delete(animeId, userAdapter.getUserId());
	}

}
