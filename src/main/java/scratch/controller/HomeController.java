package scratch.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.context.DictTypeContext;
import scratch.model.entity.Anime;
import scratch.model.ohter.DictList;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeDisplay;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

import javax.swing.text.html.Option;

@Controller
public class HomeController {
	
	private final static String[] FACECHARS = {"(ง •̀_•́)ง ","(•̀ᴗ•́)و ̑̑ ", "ヽ(•̀ω•́ )ゝ", 
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
	
	private int limit = 10;

	@ModelAttribute
	public void setModel(Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		Long userId = null;
		model.addAttribute("types",
				dictService.findByType(DictTypeContext.ANIMETYPE));
		Optional.ofNullable(userAdapter).ifPresent(adapter -> {
			model.addAttribute("searchHistories",
					animeService.listSearchHistory(adapter.getUserId()));
		});
	}
	
	/**
	 * 主页面
	 * 显示所有中最受关注的 和 各个类别受最关注的 影视
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String mainPage(Model model, 
			@AuthenticationPrincipal UserAdapter userAdapter){

		List<Anime> mostFocuseds = animeService.listMostFocused(limit);
		Map<String, List<Anime>> mostFcousedMap = animeService.listMostFcousedGroupByType(limit);

		model.addAttribute("mostFocusAnimes", wrapAnimes(mostFocuseds, userAdapter));
		model.addAttribute("typeAndAnimes", wrapAnimes(mostFcousedMap, userAdapter));
		model.addAttribute("module", "home");
		return "index";
	}
	
	/**
	 * 按类别显示影视
	 * @param code
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/{typeCode}")
	public String listAnimesByType(@PathVariable("typeCode") String code,
			Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		DictList dictList = dictService.findByType(DictTypeContext.ANIMETYPE);
		if(dictList.get(code) == null) throw new RuntimeException();

		List<Anime> mostFocuseds = animeService.listByType(code);
		model.addAttribute("animes", wrapAnimes(mostFocuseds, userAdapter));
		model.addAttribute("module", code);
		return "index";
	}
	
	/**
	 * 根据关键词搜索影视
	 * @param word
	 * @param model
	 * @param userAdapter
	 * @return
	 */
	@GetMapping("/search")
	public String search(@RequestParam("word") String word,
			Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		Long userId = Optional.ofNullable(userAdapter).orElseGet(null).getUserId();
		model.addAttribute("animes",
				wrapAnimes(animeService.listByName(word.trim(), userId), userAdapter));
		return "index";
	}
	
	/**
	 * 显示指定影视的集
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

	@GetMapping("/anime/episodess/{animeId}")
	public String showAnimeEpisodess(@PathVariable("animeId") Long animeId, Model model) {
		model.addAttribute("anime", animeService.getById(animeId));
		model.addAttribute("episodes", episodeService.listByAnimeId(animeId));
		return "/anime/episodess";
	}
	
	/**
	 * 用户处于登录状态，读取是否关注的属性
	 * @param animes
	 * @param userAdapter
	 * @return
	 */
	private Object wrapAnimes(List<Anime> animes, UserAdapter userAdapter) {
		if(userAdapter == null) {
			return animes;
		}
		return animeFocusService.getAnimeFocus(animes, userAdapter.getUserId());
	}
	
	/**
	 * 用户处于登录状态，读取是否关注的属性
	 * @param animesMap
	 * @param userAdapter
	 * @return
	 */
	private Object wrapAnimes(Map<String, List<Anime>> animesMap, UserAdapter userAdapter) {
		if(userAdapter == null) {
			return animesMap;
		}
		Map<String, List<AnimeDisplay>> animeDisplaysMap = new LinkedHashMap<>();
		animesMap.entrySet().forEach(entry -> {
			List<Anime> animes = entry.getValue();
			List<AnimeDisplay> animeDisplays = animeFocusService.getAnimeFocus(animes, userAdapter.getUserId());
			animeDisplaysMap.put(entry.getKey(), animeDisplays);
		});
		return animeDisplaysMap;
	}
	
	@RequestMapping(value="/admin", method=RequestMethod.GET)
	public ModelAndView adminPage(Model model){
		return new ModelAndView("/admin/index");
	}
	
	@RequestMapping(value="/common/message", method=RequestMethod.GET)
	public String message(RedirectAttributes ra, Model model) {
		if(model.asMap().size() == 0) {
			model.addAttribute("success",
					FACECHARS[new Random().nextInt(FACECHARS.length)]);
		}
		model.addAllAttributes(ra.getFlashAttributes());
		return "base/message";
	}
	
}
