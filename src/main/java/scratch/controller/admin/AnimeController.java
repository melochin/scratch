package scratch.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.Role;
import scratch.aspect.UserRole;
import scratch.model.Anime;
import scratch.model.AnimeAlias;
import scratch.model.DictType;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.support.Result;

@RequestMapping("/admin")
@Controller
public class AnimeController {

	private static final String WEB_INF_RESOURCE = "/WEB-INF/resource";

	@Autowired
	private AnimeService service;
	
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public void addModel(Model model) {
		model.addAttribute("module", "anime");
	}
	
	/**
	 * 维护页面
	 * @param model
	 * @return
	 */
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime")
	public String index(Model model, @RequestParam(defaultValue="1", name="p") Integer page) {
		model.addAttribute("animeList", service.find(page));
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return "/admin/anime/index";
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/save", method=RequestMethod.POST)
	public ModelAndView save(@RequestParam(value="picFile", required=false) MultipartFile file, 
			@Valid Anime anime, HttpServletRequest request) throws Exception {
		
		if(file == null || file.getSize() == 0) {
			service.save(anime);
		} else {
			String realPath = request.getServletContext().getRealPath(WEB_INF_RESOURCE);
			service.saveWithPicFile(anime, file, realPath);
		}
		
		return new ModelAndView("redirect:/admin/anime");
	}

	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/update", method=RequestMethod.POST)
	public ModelAndView update(@RequestParam(value="picFile", required=false) MultipartFile file,
			@Valid Anime anime, HttpServletRequest request) throws IllegalStateException, IOException {
		
		if(file == null || file.getSize() == 0) {
			service.update(anime);
		} else {
			String realPath = request.getServletContext().getRealPath(WEB_INF_RESOURCE);
			service.updateWithFile(anime, file, realPath);
		}
		
		return new ModelAndView("redirect:/admin/anime");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/delete/{animeId}", method=RequestMethod.GET)
	public ModelAndView delete(@PathVariable("animeId") Long animeId, HttpServletRequest request) {
		String realPath = request.getServletContext().getRealPath(WEB_INF_RESOURCE);
		service.deleteWithFile(animeId, realPath);
		return new ModelAndView("redirect:/admin/anime");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/{animeId}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Result<Anime> find(@PathVariable("animeId") Long animeId) {
		Result<Anime> result = null;
		Anime anime = service.findById(animeId);
		if(anime != null) {
			result = new Result<Anime>(anime);
		} else {
			result = new Result<Anime>("番剧不存在");
		}
		return result;
	}
	
	@UserRole(value=Role.Admin)
	@GetMapping(value="/anime/form")
	public String animeFormForNew(Model model) {
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return "/admin/anime/save";
	}

	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/form/{animeId}", method=RequestMethod.GET)
	public ModelAndView animeForm(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = service.findById(animeId);
		model.addAttribute("anime", anime);
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return new ModelAndView("/admin/anime/edit");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/link/{animeId}", method=RequestMethod.GET)
	public ModelAndView animeLink(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = service.findById(animeId);
		model.addAttribute("anime", anime);
		model.addAttribute("animeHosts", dictService.findByType(DictType.HOST));
		return new ModelAndView("/admin/anime/link");
	}
	
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/link", method=RequestMethod.POST)
	public ModelAndView linke(@RequestBody ArrayList<AnimeAlias> aliass) {
		System.out.println(Arrays.asList(aliass) );
		return new ModelAndView("/admin/anime/link");
	}
	
	
}
