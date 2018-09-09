package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scratch.model.entity.Anime;
import scratch.service.AnimeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiAnimeController {

	@Autowired
	private AnimeService animeService;

	@GetMapping("/api/admin/animes")
	public Object list(@RequestParam(value="page", required = false) Integer page) {
		if(page == null) {
			List<Anime> animes = animeService.list();
			return animes.stream()
					.map(anime -> {
						anime.setAliass(animeService.findAlias(anime.getId()));
						return anime;
					})
					.collect(Collectors.toList());
		} else {
			return animeService.pageByType(null, page);
		}
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
	public Map upload(@RequestParam("animePic") MultipartFile file) throws IOException {
		String filename = animeService.upload(file);
		Map map = new HashMap();
		map.put("filename", filename);
		return map;
	}

	@DeleteMapping("/api/admin/animes/{animeId}")
	public void remove(@PathVariable("animeId") Long animeId) {
		animeService.delete(animeId);
	}

}
