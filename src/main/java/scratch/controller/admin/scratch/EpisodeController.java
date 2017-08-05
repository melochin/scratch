package scratch.controller.admin.scratch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import scratch.model.AnimeEpisode;
import scratch.service.AnimeEpisodeService;

@SessionAttributes({"waitCount", "passCount", "rejectCount"})
@RequestMapping("/admin/scratch/episode")
@Controller
public class EpisodeController {

	@Autowired
	private AnimeEpisodeService episodeService;
	
	@ModelAttribute
	private void setModel(Model model) {
		model.addAttribute("module", "episode");
	}
	
	
	@GetMapping("")
	public String show(Model model) {
		// 显示今日通过
		List<AnimeEpisode> todayEpisodes = episodeService.listTodayPass();
		// 显示所有的
		List<AnimeEpisode> episodes = episodeService.list();
		model.addAttribute("todayEpisodes", todayEpisodes);
		model.addAttribute("episodes", episodes);
		return "/admin/scratch/episode";
	}
	
}
