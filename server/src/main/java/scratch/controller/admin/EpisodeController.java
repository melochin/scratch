package scratch.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import scratch.model.entity.AnimeEpisode;
import scratch.service.AnimeEpisodeService;
import scratch.service.AnimeService;
import scratch.service.DictService;
import scratch.service.anime.AnimePushService;

@RequestMapping("/admin/scratch/episode")
@Deprecated
public class EpisodeController {
	
	@Autowired
	private AnimeService animeService;

	@Autowired
	private AnimeEpisodeService episodeService;
	
	@Autowired
	private DictService dictService;

	@Autowired
	private AnimePushService animePushService;
	
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

	@GetMapping("/push")
	public String push() {
		animePushService.push();
		return "redirect:/admin/scratch/episode";
	}
	
}
