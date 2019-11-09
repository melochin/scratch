package scratch.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scratch.model.entity.Anime;
import scratch.service.anime.AnimeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiAnimeController {

	private AnimeService animeService;

	@Autowired
	public ApiAnimeController(AnimeService animeService) {
		this.animeService = animeService;
	}

	@GetMapping("/api/admin/animes")
	public Object list(@RequestParam(value = "page", required = false) Integer page) {

		if (page != null) return animeService.pageByType(null, page);

		List<Anime> animes = animeService.list();
		animes.forEach(anime ->
				anime.setAliass(animeService.findAlias(anime.getId()))
		);
		return animes;
	}

	@PostMapping("/api/admin/animes")
	public Anime save(@RequestBody Anime anime) {
		// 保存
		animeService.save(anime);
		// 获取新的数据
		Long animeId = anime.getId();
		Anime newAnime = animeService.findById(animeId);
		newAnime.setAliass(animeService.findAlias(animeId));
		return newAnime;
	}

	@PutMapping("/api/admin/animes")
	public Anime modify(@RequestBody Anime anime) {
		// 更新
		animeService.update(anime);
		// 获取新的数据
		Long animeId = anime.getId();
		Anime newAnime = animeService.findById(animeId);
		newAnime.setAliass(animeService.findAlias(animeId));
		return newAnime;
	}

	@PostMapping("/api/admin/animes/upload")
	public Map<String, String> upload(@RequestParam("animePic") MultipartFile file) throws IOException {
		String filename = animeService.upload(file);
		Map<String, String> map = new HashMap<>();
		map.put("filename", filename);
		return map;
	}

	@DeleteMapping("/api/admin/animes/{animeId}")
	public void remove(@PathVariable("animeId") Long animeId) {
		animeService.delete(animeId);
	}

}
