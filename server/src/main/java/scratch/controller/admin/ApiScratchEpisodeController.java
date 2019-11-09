package scratch.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.AnimeEpisode;
import scratch.model.entity.AnimeEpisodeScratch;
import scratch.service.AnimeEpisodeService;
import scratch.support.web.JsonResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			@RequestParam(value = "status") Integer status,
			@RequestParam(value = "hostId", required = false) Long hostId,
			@RequestParam(value = "animeId", required = false) Long animeId) {

		return episodeService.listScratch(status).stream()
				.filter( e -> hostId == null || hostId.equals(e.getHostId()))
				.filter( e -> animeId == null || animeId.equals(e.getAnime().getId()))
				.collect(Collectors.toList());
	}

	@GetMapping("/api/admin/scratch/episodes/repeat/{id}")
	public Map repeat(@PathVariable("id") Long id) {

		JsonResult result = new JsonResult().setValidate(true);

		AnimeEpisodeScratch scratch = episodeService.getScratch(id);
		if(scratch != null) {
			AnimeEpisode animeEpisode = episodeService.getByAnimeIdAndHostIdAndNo(
					scratch.getAnime().getId(), scratch.getHostId(), scratch.getNumber());
			if(animeEpisode != null) {
				result.setValidate(false);
				result.put("scratch", scratch);
				result.put("episode", animeEpisode);
			}
		}

		return result;
	}

	@GetMapping("/api/admin/scratch/episodes/count")
	public Map count() {
		return episodeService.listCountByStatus();
	}

	@PostMapping("/api/admin/scratch/episodes/pass/{id}")
	public void pass(@PathVariable("id") Long id) {
		episodeService.passScratch(id);
	}

	@PostMapping("/api/admin/scratch/episodes/reject/{id}")
	public void reject(@PathVariable("id") Long id) {
		episodeService.rejectScratch(id);
	}

}
