
package scratch.controller.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.model.Anime;
import scratch.model.DictType;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.support.Result;

@RequestMapping("/admin")
@Controller
public class AnimeController {

	@Autowired
	private AnimeService serivce;
	
	@Autowired
	private DictService dictService;
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime")
	public ModelAndView index(Model model) {
		model.addAttribute("animeList", serivce.findAll());
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return new ModelAndView("admin_anime");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/save", method=RequestMethod.POST)
	public ModelAndView save(@Valid Anime anime, BindingResult result) throws BindException {
		if(result.hasErrors()) {
			throw new BindException(result);
		}
		serivce.save(anime);
		return new ModelAndView("redirect:/admin/anime");
	}

	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/update", method=RequestMethod.POST)
	public ModelAndView update(Anime anime) {
		serivce.update(anime);
		return new ModelAndView("redirect:/admin/anime");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/delete/{animeId}", method=RequestMethod.GET)
	public ModelAndView delete(@PathVariable("animeId") Long animeId) {
		serivce.delete(animeId);
		return new ModelAndView("redirect:/admin/anime");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/{animeId}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Result<Anime> find(@PathVariable("animeId") Long animeId) {
		Result<Anime> result = null;
		Anime anime = serivce.findById(animeId);
		if(anime != null) {
			result = new Result<Anime>(anime);
		} else {
			result = new Result<Anime>("番剧不存在");
		}
		return result;
	}

	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/form/{animeId}", method=RequestMethod.GET)
	public ModelAndView animeForm(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = serivce.findById(animeId);
		model.addAttribute("anime", anime);
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return new ModelAndView("/admin/anime/edit");
	}
	
}
