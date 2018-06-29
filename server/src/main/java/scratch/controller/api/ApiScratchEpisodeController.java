package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;
import scratch.service.AnimeEpisodeService;

import java.util.List;

@RestController
public class ApiScratchEpisodeController {

	@Autowired
	private AnimeEpisodeService episodeService;

	/**
	 * stats : 	1 通过
	 * 			0 未检查
	 * 			-1 不通过
	 * @param status
	 * @return
	 */
	@GetMapping("/api/admin/scratch/episodes")
	public List<AnimeEpisodeScratch> list(
			@RequestParam(value = "status") Integer status) {
		return episodeService.listScratch(status);
	}

	@PostMapping("/api/admin/scratch/episodes/pass={id}")
	public void pass(@PathVariable("id") Long id) {
		episodeService.passScratch(id);
	}

	@PostMapping("/api/admin/scratch/episodes/cover/{scratchId}/{episodeId}")
	public void pass(@PathVariable("scratchId") Long scratchId,
					 @PathVariable("episodeId") Long episodeId) {
		episodeService.cover(scratchId, episodeId);
	}

	@PostMapping("/api/admin/scratch/episodes/reject={id}")
	public void reject(@PathVariable("id") Long id) {
		episodeService.rejectScratch(id);
	}

}
