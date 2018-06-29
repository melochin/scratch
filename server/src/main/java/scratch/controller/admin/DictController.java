
package scratch.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scratch.model.entity.Dict;
import scratch.service.DictService;
import scratch.support.web.JsonResult;

@RequestMapping("/admin/dic")
@Controller
public class DictController {

	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public void addModel(Model model) {
		model.addAttribute("module", "dic");
	}
	
	@GetMapping("")
	public String index(Model model) {
		model.addAttribute("dictionaries", dictService.findAllDictionaries());
		return "admin/dic/index";
	}
	

	@PostMapping(value="//addDic", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult addDic(Dict dict) {
		dict.setParentCode("-1");
		return addDicData(dict);
	}
	
	@PostMapping(value="/addDicData", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult addDicData(@RequestBody Dict dict) {
		JsonResult result = new JsonResult();
		boolean success = true;
		try{
			dictService.save(dict);
		} catch (Exception e) {
			result.setError(e.getMessage());
			success = false;
		}
		result.setSuccess(success);
		return result;
	}


	@Deprecated
	@GetMapping(value="/validate/code", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult validateCode(Dict dict) {
		JsonResult result = new JsonResult();
		try{
			Dict newDict = dictService.findByCodeAndParentCode(dict.getCode(), dict.getParentCode());
			result.setValidate(newDict == null);
		} catch (Exception e) {
			result.setError(e.getMessage());
		}
		return result;
	}

}
