package scratch.useless;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import scratch.controller.home.ApiAnimeFocusController;
import scratch.model.DictType;
import scratch.model.ohter.UserAdapter;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

@Deprecated
public class AnimeFocusController {

	@Autowired
	private ApiAnimeFocusController apiAnimeFocusController;

	@Autowired
	private AnimeFocusService service;
	
	@Autowired
	private DictService dictService;
	
	@RequestMapping(value="/focus", method=RequestMethod.GET)
	public String index(@RequestParam(value="type", required=false) String type,
			@RequestParam(value="focus", required=false) Integer focus,
			@AuthenticationPrincipal UserAdapter userAdapter,
			Model model) {
		
		if(StringUtils.isEmpty(type)) {
			type = null;
		}
		
		Long userId = userAdapter.getUserId();
		model.addAttribute("animeList", service.findAllAnime(userId, type, focus));
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		
		model.addAttribute("type", type);
		model.addAttribute("focus", focus);
		return "/anime/focus";
	}

	@GetMapping(value="/unfocus/{id}")
	public String unFocus(@PathVariable("id") Long animeId, @AuthenticationPrincipal UserAdapter userAdapter,
						@RequestHeader(name="referer", required=false, defaultValue="/") String referer) {
		//apiAnimeFocusController.unFocus(animeId, userAdapter);
		return "redirect:" + referer;
	}

}
