package scratch.controller.anime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.context.SessionContext;
import scratch.model.Anime;
import scratch.model.DictType;
import scratch.model.User;
import scratch.service.DictService;
import scratch.service.anime.AnimeFocusService;

@RequestMapping("/anime")
@Controller
public class AnimeFocusController {
	
	@Autowired
	private AnimeFocusService service;
	
	@Autowired
	private DictService dictService;
	
	@UserRole(value=Role.User)
	@RequestMapping(value="/focus", method=RequestMethod.GET)
	public String index(@RequestParam(value="type", required=false) String type,
			@RequestParam(value="focus", required=false) Integer focus,
			@SessionAttribute(value=SessionContext.USER) User user,
			Model model) {
		
		if(StringUtils.isEmpty(type)) {
			type = null;
			
		}
		Long userId = user.getUserId();
		model.addAttribute("animeList", service.findAllAnime(userId, type, focus));
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		
		model.addAttribute("type", type);
		model.addAttribute("focus", focus);
		return "/anime/focus";
	}
	
	@UserRole(value=Role.User)
	@RequestMapping(value="/focus/add", method=RequestMethod.POST)
	public ModelAndView add(@RequestParam("animeId") Long animeId,
			@SessionAttribute(SessionContext.USER) User user) {
		service.save(new Anime(animeId), user);
		return new ModelAndView("redirect:/anime/focus");
	}
	
	@UserRole(value=Role.User)
	@RequestMapping(value="/focus/delete", method=RequestMethod.POST)
	public ModelAndView delete(@RequestParam("animeId") Long animeId,
			@SessionAttribute(SessionContext.USER) User user) {
		service.delete(new Anime(animeId), user);
		return new ModelAndView("redirect:/anime/focus");
	}
	
}
