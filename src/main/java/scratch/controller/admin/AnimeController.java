package scratch.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.Anime;
import scratch.model.AnimeAlias;
import scratch.model.Dict;
import scratch.model.DictList;
import scratch.model.DictType;
import scratch.service.AnimeService;
import scratch.service.DictService;

@RequestMapping("/admin")
@Controller
public class AnimeController {

	private static final String WEB_INF_RESOURCE = "/WEB-INF/resource";

	@Autowired
	private AnimeService service;
	
	@Autowired
	private DictService dictService;

	/** 导航栏定位用  */
	@ModelAttribute
	public void addModel(Model model) {
		model.addAttribute("module", "anime");
	}
	
	/**	维护页面	*/
	@RequestMapping(value="/anime")
	public String index(@RequestParam(value="p", defaultValue="1") Integer page, 
			@RequestParam(value="type", required=false) String type, Model model) {
		model.addAttribute("animeList", service.pageByType(type, page));
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return "/admin/anime/index";
	}
	
	@GetMapping(value={"/anime/form","/anime/form/{animeId}"})
	public String animeForm(@PathVariable(required=false) Long animeId, Model model) {
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		
		if(animeId != null) {
			Anime anime = service.getById(animeId);
			model.addAttribute("anime", anime);
			return "/admin/anime/edit";	
		}
		
		return "/admin/anime/save";	
	}
	
	/**	新增处理	*/
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

	/** 更新处理	*/
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
	
	/**	删除处理	*/
	@RequestMapping(value="/anime/delete/{animeId}", method=RequestMethod.GET )
	public ModelAndView delete(@PathVariable("animeId") Long animeId, HttpServletRequest request) {
		String realPath = request.getServletContext().getRealPath(WEB_INF_RESOURCE);
		service.deleteWithFile(animeId, realPath);
		return new ModelAndView("redirect:/admin/anime");
	}
	
	/** 关联站点界面 **/
	@RequestMapping(value="/anime/link/{animeId}", method=RequestMethod.GET)
	public ModelAndView animeLink(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = service.findByIdWithAlias(animeId);
		List<AnimeAlias> aliass= anime.getAliass();
		if(aliass == null) {
			aliass = new ArrayList<AnimeAlias>();
		}
		DictList dictList = dictService.findByType(DictType.HOST);
		for(Dict dict : dictList) {
			boolean find = false;
			for(AnimeAlias a : aliass) {
				if(dict.getCode().equals(a.getHostId().toString())) {
					find = true;
				}
			}
			if(!find) {
				AnimeAlias animeAlias = new AnimeAlias() ;
				animeAlias.setHostId(Long.valueOf(dict.getCode()));
				animeAlias.setAlias("");
				animeAlias.setAnimeId(anime.getId());
				aliass.add(animeAlias);
			}
		}
		model.addAttribute("anime", anime);
		model.addAttribute("hosts", dictService.findByType(DictType.HOST));
		return new ModelAndView("/admin/anime/link");
	}
	
	/** 关联站点  **/
	@RequestMapping(value="/anime/link", method=RequestMethod.POST)
	public String link(Anime anime, @RequestHeader("referer") String referer) {
		
		anime.getAliass().forEach(a -> {
			String alias = a.getAlias();
			a.setAnimeId(anime.getId());
			// 更新或修改
			if(!StringUtils.isEmpty(alias)) {
				service.saveOrModifyAlias(a);
			} else {				
			// 删除或不变
				service.deleteAlias(a);
			}
		});
		
		return "redirect:" + referer;
	}
	
	
	
	/** Ajax Api	*/
	/*	@UserRole(value=Role.Admin)
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
		}*/
		
	/*	*//** 新增页面	*//*
		@UserRole(value=Role.Admin)
		@GetMapping(value="/anime/form")
		public String animeFormForNew(Model model) {
			model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
			return "/admin/anime/save";
		}*/
		
	
/*	*//**	编辑页面（更新）	*//*
	@UserRole(value=Role.Admin)
	@RequestMapping(value="/anime/form/{animeId}", method=RequestMethod.GET)
	public ModelAndView animeForm(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = service.findById(animeId);
		model.addAttribute("anime", anime);
		model.addAttribute("animeTypes", dictService.findByType(DictType.ANIMETYPE));
		return new ModelAndView("/admin/anime/edit");
	}*/
	
}
