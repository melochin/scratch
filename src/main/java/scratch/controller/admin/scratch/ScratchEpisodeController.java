package scratch.controller.admin.scratch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import scratch.model.AnimeEpisodeScratch;
import scratch.model.DictType;
import scratch.service.AnimeEpisodeService;
import scratch.service.DictService;
import scratch.support.web.spring.ModelUtils;

@SessionAttributes({"waitCount", "passCount", "rejectCount"})
@RequestMapping("/admin/scratch/temp")
@Controller
public class ScratchEpisodeController {
	
	@Autowired
	private AnimeEpisodeService episodeService;
	
	@Autowired
	private DictService dictService;
	
	private boolean needQueryCount = true;
	
	/** 待审核、通过审核、失败审核的总数 */
	@ModelAttribute
	private void setModel(Model model) {
		// 数据发生变化时，才会触发重新查询
		if(needQueryCount) {
			// 获取待审核、通过审核、失败审核的记录总数
			Integer waitCount = episodeService.countByScratchStatus(new Integer(0));
			Integer passCount = episodeService.countByScratchStatus(new Integer(1));
			Integer rejectCount = episodeService.countByScratchStatus(new Integer(-1));
			model.addAttribute("waitCount", waitCount);
			model.addAttribute("passCount", passCount);
			model.addAttribute("rejectCount", rejectCount);
			needQueryCount = false;
		}
		// nav menu active item
		model.addAttribute("module", "temp");
	}
	
	/** 显示各个状态的数据 */
	@GetMapping("")
	public String showEpisodeScratch(@RequestParam(value="status", required=false, defaultValue="0") 
	Integer status, Model model) {
		model.addAttribute("episodeScratchs", episodeService.listScratch(status));
		model.addAttribute("status", status);
		model.addAttribute("hostMap", dictService.findByType(DictType.HOST).asMap());
		return "/admin/scratch/temp";
	}

	/** 审核通过 **/
	@GetMapping("/pass")
	public String pass(@RequestParam(value="id") Long id, 
			@RequestHeader("Referer") String referer,
			RedirectAttributes ra) {
		episodeService.passScratch(id);
		needQueryCount = true;
		ModelUtils.setSuccess(ra, "操作成功");
		return redirect(referer);
	}
	
	/** 审核通过，传递{@link AnimeEpisodeScratch}实体，能够更改数据 **/
	@PostMapping("/pass/modify")
	public String passAndModify(AnimeEpisodeScratch episodeScratch,
			@RequestHeader("Referer") String referer,
			RedirectAttributes ra) {
		episodeService.passScratch(episodeScratch);
		needQueryCount = true;
		ModelUtils.setSuccess(ra, "操作成功");
		return redirect(referer);
	}
	
	/** 拒绝通过 **/
	@GetMapping("/reject")
	public String reject(@RequestParam(value="id") Long id, 
			@RequestHeader("Referer") String referer,
			RedirectAttributes ra) {
		episodeService.rejectScratch(id);
		needQueryCount = true;
		ModelUtils.setSuccess(ra, "操作成功");
		return redirect(referer);
	}
	
	/** 重定向判断，如果referer含有关联链接，则重定向referer，否则按默认的链接走  **/
	private String redirect(String referer) {
		String defaultRedirect = "/admin/scratch/temp";
		if(referer.indexOf(defaultRedirect) > 0) {
			return "redirect:" + referer;
		}
		return defaultRedirect;
	}
	
}
