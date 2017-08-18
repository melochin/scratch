package scratch.controller.anime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.context.DictTypeContext;
import scratch.context.SessionContext;
import scratch.model.entity.Anime;
import scratch.model.entity.User;
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
		
		Long userId = userAdapter.getUser().getUserId();
		model.addAttribute("animeList", service.findAllAnime(userId, type, focus));
		model.addAttribute("animeTypes", dictService.findByType(DictTypeContext.ANIMETYPE));
		
		model.addAttribute("type", type);
		model.addAttribute("focus", focus);
		return "/anime/focus";
	}
	
	@RequestMapping(value="/focus/add", method=RequestMethod.POST)
	public String add(@RequestParam("animeId") Long animeId,
			@AuthenticationPrincipal UserAdapter userAdapter, 
			@RequestHeader("referer") String referer) {
		service.save(new Anime(animeId), userAdapter.getUser());
		return "redirect:"+referer;
	}
	
	@RequestMapping(value="/focus/delete", method=RequestMethod.POST)
	public String delete(@RequestParam("animeId") Long animeId,
			@AuthenticationPrincipal UserAdapter userAdapter, 
			@RequestHeader("referer") String referer) {
		service.delete(new Anime(animeId), userAdapter.getUser());
		return "redirect:"+referer;
	}
	
}
