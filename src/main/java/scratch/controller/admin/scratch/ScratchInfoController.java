package scratch.controller.admin.scratch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scratch.service.anime.AnimeScratchService;

@Controller
public class ScratchInfoController {

	@Autowired
	private AnimeScratchService scratchService;
	
	@ModelAttribute
	public void setModel(Model model) {
		model.addAttribute("module", "scratch");
	}
	
	@GetMapping("/admin/scratch")
	public String showRecord(Model model) {
		// 目前服务是否运行
		boolean isRun = scratchService.isRun();
		model.addAttribute("times", isRun);
		// 最近运行记录
		Map<String,Integer> map = scratchService.getRecordMap();
		model.addAttribute("times", map.keySet());
		model.addAttribute("counts", map.values());
		return "/admin/scratch/index";
	}

}
