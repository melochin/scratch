package scratch.controller.user;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.DictType;
import scratch.model.entity.Anime;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeDisplay;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

@Controller
public class HomeController {

	private final static String[] FACECHARS = {"(ง •̀_•́)ง ", "(•̀ᴗ•́)و ̑̑ ", "ヽ(•̀ω•́ )ゝ",
			"(,,• ₃ •,,)", "(｡˘•ε•˘｡)", " (=ﾟωﾟ)ﾉ", "(╯‵□′)╯︵┻━┻", "ㄟ( ▔, ▔ )ㄏ", "(*´Д｀*) ",
			"(°□°；) ", "∑(っ °Д °;)っ"};

	@Autowired
	private AnimeService animeService;

	@Autowired
	private AnimeFocusService animeFocusService;

	@Autowired
	private AnimeEpisodeService episodeService;

	@Autowired
	private DictService dictService;

	private final static int LIMIT = 10;

	@ModelAttribute
	public void setModel(Model model,
						 @AuthenticationPrincipal UserAdapter userAdapter) {
		model.addAttribute("types",
				dictService.findByType(DictType.ANIMETYPE));

		if (userAdapter != null) {
			model.addAttribute("searchHistories",
					animeService.listSearchHistory(userAdapter.getUserId()));
		}
	}

	/**
	 * 主页面
	 * 显示所有中最受关注的 和 各个类别受最关注的 影视
	 *
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model,
					   @AuthenticationPrincipal UserAdapter userAdapter) {

		List<Anime> mostFocuseds = animeService.listMostFocused(LIMIT);
		Map<String, List<Anime>> mostFcousedMap = animeService.listMostFcousedGroupByType(LIMIT);

		model.addAttribute("mostFocusAnimes", ifUserPresentConvertAnime(mostFocuseds, userAdapter));
		model.addAttribute("typeAndAnimes", ifUserPresentConvertAnime(mostFcousedMap, userAdapter));
		model.addAttribute("module", "home");
		return "index";
	}

	/**
	 * 按类别显示影视
	 *
	 * @param typeCode    类型代码
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@GetMapping("/{typeCode}")
	public String listAnimesByType(@PathVariable("typeCode") String typeCode,
								   Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		// 验证类型是否存在
		if (dictService.findByCodeAndParentCode(typeCode, DictType.ANIMETYPE) == null) {
			throw new RuntimeException();
		}
		List<Anime> animes = animeService.listByType(typeCode);
		model.addAttribute("animes", ifUserPresentConvertAnime(animes, userAdapter));
		model.addAttribute("module", typeCode);
		return "index";
	}

	/**
	 * 根据关键词搜索影视
	 *
	 * @param word
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@GetMapping("/search")
	public String search(@RequestParam("word") String word,
						 Model model, @AuthenticationPrincipal UserAdapter userAdapter) {

		if (userAdapter != null) {
			animeService.addSearchHistory(word, userAdapter.getUserId());
		}

		model.addAttribute("animes",
				ifUserPresentConvertAnime(animeService.listByName(word.trim()), userAdapter));
		return "index";
	}

	/**
	 * 显示指定影视的集
	 *
	 * @param animeId
	 * @param model
	 * @return
	 */
	@GetMapping("/anime/episodes/{animeId}")
	public String showAnimeEpisodes(@PathVariable("animeId") Long animeId, Model model) {
		model.addAttribute("anime", animeService.getById(animeId));
		model.addAttribute("episodes", episodeService.listByAnimeId(animeId));
		return "/anime/episodes";
	}

	/**
	 * 用户处于登录状态，读取关注属性
	 *
	 * @param animes
	 * @param userAdapter
	 * @return
	 */
	private Object ifUserPresentConvertAnime(List<Anime> animes, UserAdapter userAdapter) {
		if (userAdapter == null) return animes;
		return animeFocusService.getAnimeFocus(animes, userAdapter.getUserId());
	}

	/**
	 * 用户处于登录状态，读取关注属性
	 *
	 * @param animesMap   key: video type value : List<Anime>
	 * @param userAdapter
	 * @return
	 */
	private Object ifUserPresentConvertAnime(Map<String, List<Anime>> animesMap, UserAdapter userAdapter) {
		if (userAdapter == null) return animesMap;

		Map<String, List<AnimeDisplay>> animeDisplaysMap = animesMap.entrySet()
				.stream()
				.collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry -> animeFocusService.getAnimeFocus(entry.getValue(), userAdapter.getUserId())
				));
		return animeDisplaysMap;
	}

	/**
	 * 管理员后台主页
	 *
	 * @return
	 */
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin() {
		return "/admin/index";
	}

	@RequestMapping(value = "/common/message", method = RequestMethod.GET)
	public String message(RedirectAttributes ra, Model model) {
		if (model.asMap().size() == 0) {
			model.addAttribute("success",
					FACECHARS[new Random().nextInt(FACECHARS.length)]);
		}
		model.addAllAttributes(ra.getFlashAttributes());
		return "base/message";
	}

}
