package scratch.controller.anime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.model.Anime;
import scratch.service.anime.AnimeFocusService;
import scratch.support.web.SessionSupport;

@RequestMapping("/anime")
@Controller
public class AnimeFocusController {
	
	private AnimeFocusService service;
	
	@UserRole(value=Role.User)
	@RequestMapping("/focus")
	public ModelAndView index(Model model) {
		Long userId = SessionSupport.getUser().getUserId();
		model.addAttribute("animeList", service.findAllAnime(userId));
		return new ModelAndView("anime-focus");
	}
	
	@UserRole(value=Role.User)
	@RequestMapping(value="/focus/add", method=RequestMethod.POST)
	public ModelAndView add(@RequestParam("animeId") Long animeId) {
		service.save(new Anime(animeId), SessionSupport.getUser());
		return new ModelAndView("redirect:/focus");
	}
	
	@UserRole(value=Role.User)
	@RequestMapping(value="/focus/delete", method=RequestMethod.POST)
	public ModelAndView delete(@RequestParam("animeId") Long animeId) {
		service.delete(new Anime(animeId), SessionSupport.getUser());
		return new ModelAndView("redirect:/focus");
	}
	
}