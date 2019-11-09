package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import scratch.model.ohter.UserAdapter;
import scratch.service.VoteService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiVoteController {

	@Autowired
	private VoteService voteService;

	@PostMapping("/api/votes/{episodeId}")
	public Map<String, Long> vote(@PathVariable("episodeId") Long episodeId,
					 @AuthenticationPrincipal UserAdapter principal) {
		Map<String, Long> result = new HashMap<String, Long>();
		result.put("votes", voteService.vote(principal.getUserId(), episodeId));
		return result;
	}

	@DeleteMapping("/api/votes/{episodeId}")
	public Map<String, Long> cancelVote(@PathVariable("episodeId") Long episodeId,
								  @AuthenticationPrincipal UserAdapter principal) {
		Map<String, Long> result = new HashMap<String, Long>();
		result.put("votes", voteService.cancelVote(principal.getUserId(), episodeId));
		return result;
	}

}
