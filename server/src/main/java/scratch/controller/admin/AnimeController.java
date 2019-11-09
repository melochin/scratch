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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.DictType;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.model.entity.Dict;
import scratch.model.ohter.DictList;
import scratch.service.AnimeService;
import scratch.service.DictService;

@RequestMapping("/admin")
@Deprecated
public class AnimeController {

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

	
	/**	新增处理	*/
	@RequestMapping(value="/anime/save", method=RequestMethod.POST)
	public ModelAndView save(@RequestParam(value="picFile", required=false) MultipartFile file, 
			@Valid Anime anime, HttpServletRequest request) throws Exception {
		
		if(file == null || file.getSize() == 0) {
			service.save(anime);
		} else {
			service.saveWithPicFile(anime, file);
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
			service.updateWithFile(anime, file);
		}
		
		return new ModelAndView("redirect:/admin/anime");
	}
	
	/**	删除处理	*/
	@RequestMapping(value="/anime/delete/{animeId}", method=RequestMethod.GET )
	public ModelAndView delete(@PathVariable("animeId") Long animeId, HttpServletRequest request) {
		service.deleteWithFile(animeId);
		return new ModelAndView("redirect:/admin/anime");
	}
	
//	/** 关联站点界面 **/
//	@RequestMapping(value="/anime/link/{animeId}", method=RequestMethod.GET)
//	public ModelAndView animeLink(@PathVariable("animeId") Long animeId, Model model) {
//		Anime anime = service.findByIdWithAlias(animeId);
//		List<AnimeAlias> aliass= anime.getAliass();
//		if(aliass == null) {
//			aliass = new ArrayList<AnimeAlias>();
//		}
//		DictList dictList = dictService.findByType(DictType.HOST);
//		for(Dict dict : dictList) {
//			boolean find = false;
//			for(AnimeAlias a : aliass) {
//				if(dict.getCode().equals(a.getHostId().toString())) {
//					find = true;
//				}
//			}
//			if(!find) {
//				AnimeAlias animeAlias = new AnimeAlias() ;
//				animeAlias.setHostId(Long.valueOf(dict.getCode()));
//				animeAlias.setAlias("");
//				animeAlias.setAnimeId(anime.getId());
//				aliass.add(animeAlias);
//			}
//		}
//		model.addAttribute("anime", anime);
//		model.addAttribute("hosts", dictService.findByType(DictType.HOST));
//		return new ModelAndView("/admin/anime/link");
//	}


	@GetMapping("anime/upload/{animeId}")
	public ModelAndView uploadForm(@PathVariable("animeId") Long animeId, Model model) {
		Anime anime = service.findById(animeId);
		model.addAttribute(anime);
		return new ModelAndView("/admin/anime/upload");
	}

	@PostMapping(value = "/anime/upload/{animeId}")
	public @ResponseBody String upload(@RequestParam("picFile") MultipartFile file,
			@PathVariable("animeId") Long animeId, HttpServletRequest request) throws IOException {
		Anime anime = service.findById(animeId);
		service.updateWithFile(anime, file);
		return "success";
	}

	/** 关联站点  **/
//	@RequestMapping(value="/anime/link", method=RequestMethod.POST)
//	public @ResponseBody void link(Anime anime) {
//
//		anime.getAliass().forEach(a -> {
//			String alias = a.getAlias();
//			a.setAnimeId(anime.getId());
//			// 更新或修改
//			if(!StringUtils.isEmpty(alias)) {
//				service.saveOrModifyAlias(a);
//			} else {
//			// 删除或不变
//				service.deleteAlias(a);
//			}
//		});
//		return;
//	}


	
}
