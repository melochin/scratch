package scratch.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.DictType;
import scratch.service.AnimeEpisodeService;
import scratch.service.DictService;
import scratch.support.web.spring.ModelUtils;

@SessionAttributes({"waitCount", "passCount", "rejectCount"})
@RequestMapping("/admin/episode")
@Controller
public class EpisodeController {
	
	@Autowired
	private AnimeEpisodeService episodeService;
	
	@Autowired
	private DictService dictService;
	
	private boolean needQueryCount = true;
	
	@ModelAttribute
	private void setModel(Model model) {
		if(needQueryCount) {
			Integer waitCount = episodeService.countByScratchStatus(new Integer(0));
			Integer passCount = episodeService.countByScratchStatus(new Integer(1));
			Integer rejectCount = episodeService.countByScratchStatus(new Integer(-1));
			model.addAttribute("waitCount", waitCount);
			model.addAttribute("passCount", passCount);
			model.addAttribute("rejectCount", rejectCount);
			needQueryCount = false;
		}
		model.addAttribute("module", "episode");
	}
	
	@GetMapping("/scratch")
	public String showEpisodeScratch(@RequestParam(value="status", required=false, defaultValue="0") 
	Integer status, Model model) {
		model.addAttribute("episodeScratchs", episodeService.listScratch(status));
		model.addAttribute("status", status);
		model.addAttribute("hostMap", dictService.findByType(DictType.HOST).asMap());
		return "/admin/episode/scratch";
	}

	@GetMapping("/scratch/pass")
	public String pass(@RequestParam(value="id") Long id, 
			@RequestHeader("Referer") String referer,
			RedirectAttributes ra) {
		episodeService.passScratch(id);
		needQueryCount = true;
		ModelUtils.setSuccess(ra, "操作成功");
		return redirect(referer);
	}
	
	@GetMapping("/scratch/reject")
	public String reject(@RequestParam(value="id") Long id, 
			@RequestHeader("Referer") String referer,
			RedirectAttributes ra) {
		episodeService.rejectScratch(id);
		needQueryCount = true;
		ModelUtils.setSuccess(ra, "操作成功");
		return redirect(referer);
	}
	
	private String redirect(String referer) {
		if(referer.indexOf("/admin/episode/scratch") > 0) {
			return "redirect:" + referer;
		}
		return "redirect:/admin/episode/scratch";
	}
	
}
