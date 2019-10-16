package scratch.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import scratch.model.entity.Anime;
import scratch.model.entity.Dict;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeDisplay;
import scratch.model.view.AnimeEpisodeDisplay;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CenterController {

	@Autowired
	private AnimeService animeService;

	@Autowired
	private AnimeEpisodeService episodeService;

	@Autowired
	private DictService dictService;

	/**
	 * 用户中心
	 * 显示推荐anime
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("/center")
	public String center(Model model) {
		List<Anime> animes = animeService.listMostFocused(5);
		model.addAttribute("recommondedAnimes", animes);
		model.addAttribute("types", dictService.findByType("02"));
		return "/user/index";
	}

	/**
	 * 根据关键词搜索影视
	 *
	 * @param word
	 * @param userAdapter
	 * @param model
	 *
	 * @return
	 */
	@GetMapping("/search")
	public String search(@RequestParam(value="word", required = false) String word,
						 @RequestParam(value = "type", required = false) String type,
						 @AuthenticationPrincipal UserAdapter userAdapter,
						 Model model) {

		// 添加搜索记录
		if (userAdapter != null) {
			animeService.addSearchHistory(word, userAdapter.getUserId());
		}

		List<Dict> types = dictService.findByType("02");

		List<AnimeDisplay> animeDisplayList = animeService.list(word, type, userAdapter);

		// 用于显示页面标题
		model.addAttribute("keyword", word);
		model.addAttribute("animes", animeDisplayList);
		model.addAttribute("types", types);
		model.addAttribute("searchType", type);
		return "/search";
	}


	/**
	 * anime的主页
	 *
	 * @param animeId
	 * @param model
	 * @return
	 */
	@GetMapping("/anime/{animeId}")
	public String showAnimeEpisodes(@PathVariable("animeId") Long animeId,
									@AuthenticationPrincipal UserAdapter userAdapter,
									Model model) {
		model.addAttribute("anime", animeService.convertAnimeDisplay(animeService.findById(animeId), userAdapter));
		model.addAttribute("episodes", episodeService.listByAnimeId(animeId));
		return "/anime/episodes";
	}

	/**
	 * 通过服务器重定向episode url，统计episode的访问次数
	 *
	 * @param episodeId		episode id
	 * @param url			episoide url
	 * @param request		获取请求IP
	 * @return
	 */
	@GetMapping("/episode/{id}/redirect")
	public String redirectEpisode(@PathVariable("id") Long episodeId,
								  @RequestParam("url") String url,
								  HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		episodeService.countHot(episodeId, ip);
		return "redirect:" + url;
	}

}
