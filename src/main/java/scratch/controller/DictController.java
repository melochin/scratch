
package scratch.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.model.Dict;
import scratch.service.DictService;
import scratch.support.web.JsonResult;

@Controller
public class DictController {

	@Autowired
	private DictService dictService;
	
	@GetMapping("/dic")
	public String index(Model model) {
		model.addAttribute("dictionaries", dictService.findAllDictionaries());
		return "/admin/dic/index";
	}
	
	@GetMapping(value="/dic/parentcode/{parentCode}", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult getDicts(@PathVariable("parentCode") String parentCode) {
		JsonResult result = new JsonResult();
		result.put("code", parentCode);
		result.put("data", dictService.findByParentCode(parentCode));
		return result;
	}
	
	@PostMapping(value="/dict/update", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult update(Dict dict) {
		JsonResult result = new JsonResult();
		boolean success = true;
		try{
			dictService.update(dict);
		} catch (Exception e) {
			result.setError(e.getMessage());
			success = false;
		}
		result.setSuccess(success);
		return result;
	}
	
	@PostMapping(value="/dict/addDic", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult addDic(Dict dict) {
		dict.setParentCode("-1");
		return addDicData(dict);
	}
	
	@PostMapping(value="/dict/addDicData", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult addDicData(Dict dict) {
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
	
	@PostMapping(value="/dict/delete", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult delete(Dict dict) {
		JsonResult json = new JsonResult();
		return json.setSuccess(dictService.delete(dict) == 1);
	}
	
	@GetMapping(value="/dict/validate/code", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> validateCode(Dict dict) {
		JsonResult result = new JsonResult();
		try{
			Dict newDict = dictService.findByCodeAndParentCode(dict.getCode(), dict.getParentCode());
			if(newDict != null) {
				result.setValidate(false);
			} else {
				result.setValidate(true);
			}
		} catch (Exception e) {
			result.setError(e.getMessage());
		}
		return result;
	}
	
}
