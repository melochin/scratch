package scratch.controller.admin.scratch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.Anime;
import scratch.model.AnimeEpisode;
import scratch.model.DictList;
import scratch.model.DictType;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.support.web.spring.ModelUtils;

@RequestMapping("/admin/scratch/episode")
@Controller
public class EpisodeController {
	
	@Autowired
	private AnimeService animeService;

	@Autowired
	private AnimeEpisodeService episodeService;
	
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	private void setModel(Model model) {
		model.addAttribute("module", "episode");
	}
	
	/** 显示{@link AnimeEpisode}所有数据 */
	@GetMapping("")
	public String show(Model model) {
		// 今日通过		
		List<AnimeEpisode> todayEpisodes = episodeService.listTodayPass();
		// 所有
		List<AnimeEpisode> episodes = episodeService.list();
		model.addAttribute("todayEpisodes", todayEpisodes);
		model.addAttribute("episodes", episodes);
		return "/admin/scratch/episode";
	}
	
	@GetMapping("/save")
	public String saveForm(Model model) {
		DictList hosts = dictService.findByType(DictType.HOST);
		List<Anime> animes = animeService.list();
		
		model.addAttribute("hosts", hosts);
		model.addAttribute("animes", animes);
		return "/admin/scratch/save";
	}
	
	/** 保存 */
	@PostMapping("/save")
	public String save(AnimeEpisode episode) {
		episodeService.save(episode);
		return "redirect:/admin/scratch/episode";
	}
	
	
	@GetMapping("/modify/{id}")
	public String modifyForm(@PathVariable("id") Long id, Model model) {
		AnimeEpisode episode = episodeService.getById(id);
		if(episode == null) {
			throw new RuntimeException("错误：找不到影视数据");
		}
		
		DictList hosts = dictService.findByType(DictType.HOST);
		model.addAttribute("episode", episode);
		model.addAttribute("hosts", hosts);
		return "/admin/scratch/edit";
	}
	
	
	/** 更新 */
	@PostMapping("/modify")
	public String modify(AnimeEpisode episode, RedirectAttributes ra) {
		if(!episodeService.modify(episode)) {
			ModelUtils.setError(ra, "更新失败");
		}
		return "redirect:/admin/scratch/episode";
	}
	
	/** 删除 */
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long episodeId) {
		episodeService.delete(episodeId);
		return "redirect:/admin/scratch/episode"; 
	}
	
}
