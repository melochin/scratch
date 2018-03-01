package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;

import java.util.List;

@RestController
public class ApiEpisodeController {

	@Autowired
	private AnimeEpisodeService episodeService;

	@Autowired
	private AnimeService animeService;

	@GetMapping("/api/admin/episodes")
	public List<AnimeEpisode> get(
			@RequestParam(value = "animeId", required = false) Long animeId) {
		if(animeId != null) {
			return episodeService.listByAnimeId(animeId);
		} else {
			return episodeService.list();
		}
	}

	@PostMapping("/api/admin/episodes")
	public AnimeEpisode save(@RequestBody AnimeEpisode episode) {
		episodeService.save(episode);
		return episode;
	}

	@PutMapping("/api/admin/episodes")
	public AnimeEpisode modify(@RequestBody AnimeEpisode episode) {
		episodeService.modify(episode);
		return episode;
	}

	@DeleteMapping("/api/admin/episodes/{id}")
	public void delete(@PathVariable("id") Long episodeId) {
		episodeService.delete(episodeId);
		return;
	}

}
