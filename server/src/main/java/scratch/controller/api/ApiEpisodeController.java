package scratch.controller.api;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeDisplay;
import scratch.model.view.AnimeEpisodeDisplay;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.support.service.Page;
import scratch.support.service.PageBean;
import scratch.support.web.JsonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiEpisodeController {

	@Autowired
	private AnimeEpisodeService episodeService;

	@Autowired
	private AnimeService animeService;

	/**
	 * 分页查询 用户关注anime的episode
	 * @param userAdapter
	 * @param pageNo
	 * @return
	 */
	@GetMapping("/api/user/episodes/page/{pageNo}")
	public PageBean<AnimeEpisodeDisplay> page(@AuthenticationPrincipal UserAdapter userAdapter,
									   @PathVariable("pageNo") Integer pageNo) {

		Long userId = userAdapter.getUserId();

		PageBean<AnimeEpisode> pageEpisodes = episodeService.pageByFocusUser(userId, pageNo);

		List<AnimeEpisodeDisplay> displays = pageEpisodes.getData()
				// 转换成 AnimeEpisodeDisplay
				.stream().map(e -> episodeService.convertToDisplay(e, userAdapter))
				// 抓取时间 递减排序
				.sorted((d1, d2) -> d1.getScratchTime().after(d2.getScratchTime()) ? -1 : 1)
				.collect(Collectors.toList());

		return new PageBean<AnimeEpisodeDisplay>(displays, pageEpisodes);
	}

	/**
	 * 分页查询 用户关注anime的episode
	 * 指定起始页到终止页的数据
	 *
	 * 最后返回的页信息为终止页的
	 *
	 * @param userAdapter
	 * @param pageStart
	 * @param pageEnd
	 * @return
	 */
	@GetMapping("/api/user/episodes/page/{pageStart}/{pageEnd}")
	public PageBean<AnimeEpisodeDisplay> page(@AuthenticationPrincipal UserAdapter userAdapter,
									   @PathVariable("pageStart") Integer pageStart,
									   @PathVariable("pageEnd") Integer pageEnd) {

		Long userId = userAdapter.getUserId();

		PageBean<AnimeEpisode> pageEpisodes = episodeService.pageByFocusUser(userId, pageStart, pageEnd);

		List<AnimeEpisodeDisplay> displays = pageEpisodes.getData()
				// 转换成 AnimeEpisodeDisplay
				.stream().map(e -> episodeService.convertToDisplay(e, userAdapter))
				// 抓取时间 递减排序
				.sorted((d1, d2) -> d1.getScratchTime().after(d2.getScratchTime()) ? -1 : 1)
				.collect(Collectors.toList());

		return new PageBean<AnimeEpisodeDisplay>(displays, pageEpisodes);
	}



	/*---------------------------------------------后台管理-------------------------------------*/

	@GetMapping("/api/admin/episodes")
	public List<AnimeEpisode> get(
			@RequestParam(value = "animeId", required = false) Long animeId,
			@RequestParam(value ="hostId", required = false) Long hostId,
			@RequestParam(value = "url", required = false) String url) {

		if (url != null) {
			List<AnimeEpisode> animeEpisodes = new ArrayList<>();
			animeEpisodes.add(episodeService.listByUrl(url));
			return animeEpisodes;
		}

		return episodeService.list().stream()
				.filter(e -> animeId == null || e.getAnime().getId().equals(animeId))
				.filter(e -> hostId == null || e.getHostId().equals(hostId))
				.collect(Collectors.toList());
	}

	@PostMapping("/api/admin/episodes")
	public AnimeEpisode save(@RequestBody AnimeEpisode episode) {
		episodeService.save(episode);
		return episodeService.getById(episode.getId());
	}

	@PutMapping("/api/admin/episodes")
	public AnimeEpisode modify(@RequestBody AnimeEpisode episode) {
		episodeService.modify(episode);
		return episodeService.getById(episode.getId());
	}

	@DeleteMapping("/api/admin/episodes/{id}")
	public void delete(@PathVariable("id") Long episodeId) {
		episodeService.delete(episodeId);
		return;
	}

	/*---------------------------------------------校验-------------------------------------*/

	/**
	 * 唯一性校验
	 * @param animeId
	 * @param hostId
	 * @param no
	 * @return
	 */
	@GetMapping("/api/admin/episodes/validate/unique")
	public JsonResult unique(@RequestParam("animeId") Long animeId,
							 @RequestParam("hostId") Long hostId,
							 @RequestParam("no") String no) {
		JsonResult result = new JsonResult();
		AnimeEpisode episode = episodeService
				.getByAnimeIdAndHostIdAndNo(animeId, hostId, no);
		if(episode == null) {
			result.setValidate(true);
		} else {
			result.setValidate(false);
			result.put("episode", episode);
		}
		return result;
	}

}
