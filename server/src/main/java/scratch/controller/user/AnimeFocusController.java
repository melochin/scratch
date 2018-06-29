package scratch.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import scratch.model.DictType;
import scratch.model.ohter.UserAdapter;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

@RequestMapping("/anime")
@Controller
public class AnimeFocusController {
	
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

}
