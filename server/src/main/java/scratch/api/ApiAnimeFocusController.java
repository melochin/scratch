package scratch.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.model.ohter.UserAdapter;
import scratch.service.anime.AnimeFocusService;

@RestController
public class ApiAnimeFocusController {

	@Autowired
	private AnimeFocusService service;

	@GetMapping("/api/user/animes/{animeId}/focus")
	public void add(@PathVariable("animeId") Long animeId,
					  @AuthenticationPrincipal UserAdapter userAdapter) {
		service.save(animeId, userAdapter.getUserId());
	}

	@GetMapping("/api/user/animes/{animeId}/unfocus")
	public void delete(@PathVariable("animeId") Long animeId,
						 @AuthenticationPrincipal UserAdapter userAdapter) {
		service.delete(animeId, userAdapter.getUserId());
	}

}
