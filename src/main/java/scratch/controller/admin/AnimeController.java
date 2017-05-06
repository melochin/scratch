package scratch.controller.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.model.Anime;
import scratch.service.AnimeService;
import scratch.support.Result;

@RequestMapping("/admin")
@Controller
public class AnimeController {

	@Autowired
	private AnimeService serivce;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime")
	public ModelAndView index(Model model) {
		model.addAttribute("animeList", serivce.findAll());
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
	
}
