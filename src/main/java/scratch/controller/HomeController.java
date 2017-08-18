package scratch.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

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
	
	/**
	 * 显示主页
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView mainPage(Model model){
		model.addAttribute("mostFocusAnimes", animeService.listMostFocused(10));
		model.addAttribute("typeAndAnimes", animeService.listMostFcousedGroupByType(10));
		model.addAttribute("types", dictService.findByType(DictTypeContext.ANIMETYPE));
		model.addAttribute("module", "home");
		return new ModelAndView("index");
	}
	
	@GetMapping("/{typeCode}")
	public String listAnimesByType(@PathVariable("typeCode") String code,
			Model model, @AuthenticationPrincipal UserAdapter userAdapter) {

		// 校验类型是否正确
		DictList dictList = dictService.findByType(DictTypeContext.ANIMETYPE);
		if(dictList.get(code) == null) {
			throw new RuntimeException();
		}
		model.addAttribute("animes", wrapAnimes(animeService.listByType(code), userAdapter));
		model.addAttribute("types", dictList);
		model.addAttribute("module", code);
		return "index";
	}
	
	@GetMapping("/search")
	public String search(@RequestParam("word") String word,
			Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		word = word.trim(); 		
		model.addAttribute("animes", wrapAnimes(animeService.listByName(word), userAdapter));
		model.addAttribute("types", dictService.findByType(DictTypeContext.ANIMETYPE));
		return "index";
	}
	
	
	@GetMapping("/anime/episodes/{animeId}")
	public String showAnimeEpisodes(@PathVariable("animeId") Long animeId, Model model) {
		model.addAttribute("anime", animeService.getById(animeId));
		model.addAttribute("episodes", episodeService.listByAnimeId(animeId));
		return "/anime/episodes";
	}
	
	private Object wrapAnimes(List<Anime> animes, UserAdapter userAdapter) {
		if(userAdapter != null) {
			return animeFocusService.getAnimeFocus(animes, userAdapter.getUser().getUserId());
		}
		return animes;
	}
	
	@RequestMapping(value="/admin", method=RequestMethod.GET)
	public ModelAndView adminPage(Model model){
		return new ModelAndView("/admin/index");
	}
	
	/**
	 * 显示信息
	 * @return
	 */
	@RequestMapping(value="/common/message", method=RequestMethod.GET)
	public String message(RedirectAttributes ra, Model model) {
		if(model.asMap().size() == 0) {
			Random random = new Random();
			model.addAttribute("success", FACECHARS[random.nextInt(FACECHARS.length)]);
		}
		model.addAllAttributes(ra.getFlashAttributes());
		return "base/message";
	}
	
}
