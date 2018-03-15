package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiEpisodeController {

	@Autowired
	private AnimeEpisodeService episodeService;

	@Autowired
	private AnimeService animeService;

	@GetMapping("/api/admin/episodes")
	public List<AnimeEpisode> get(
			@RequestParam(value = "animeId", required = false) Long animeId,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "hostId", required = false) Long hostId,
			@RequestParam(value = "no", required = false) String no) {

		if (url != null) {
			List<AnimeEpisode> animeEpisodes = new ArrayList<>();
			animeEpisodes.add(episodeService.listByUrl(url));
			return animeEpisodes;
		}

		if (animeId != null && hostId != null && no != null) {
			List<AnimeEpisode> animeEpisodes = new ArrayList<>();
			animeEpisodes.add(
					episodeService.getByAnimeIdAndHostIdAndNo(animeId, hostId, no));
			return animeEpisodes;
		}

		if (animeId != null) {
			return episodeService.listByAnimeId(animeId);
		}

		return episodeService.list();
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
