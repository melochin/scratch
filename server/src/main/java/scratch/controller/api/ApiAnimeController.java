package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.model.entity.Anime;
import scratch.service.AnimeService;

@RestController
public class ApiAnimeController {

	@Autowired
	private AnimeService animeService;

	@GetMapping("/api/admin/animes")
	public Object list(@RequestParam(value="page", required = false) Integer page) {
		if(page == null) {
			return animeService.list();
		} else {
			return animeService.pageByType(null, page);
		}
	}

	@PostMapping("/api/admin/animes")
	public Anime save(@RequestBody Anime anime) {
		animeService.save(anime);
		return anime;
	}

	@PutMapping("/api/admin/animes")
	public Anime modify(@RequestBody Anime anime) {
		animeService.update(anime);
		return anime;
	}

	@DeleteMapping("/api/admin/animes/{animeId}")
	public void remove(@PathVariable("animeId") Long animeId) {
		animeService.delete(animeId);
	}

}
