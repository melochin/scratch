package scratch.controller;

import java.util.HashMap;
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
import scratch.model.JsonResult;
import scratch.service.DictService;

@Controller
public class DictController {

	@Autowired
	private DictService dictService;
	
	@GetMapping("/dic")
	public String index(Model model) {
		model.addAttribute("dictionaries", dictService.findAllDictionaries());
		System.out.println("Hello");
		return "dic_index";
	}
	
	@GetMapping(value="/dic/parentcode/{parentCode}", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getDicts(@PathVariable("parentCode") String parentCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", parentCode);
		result.put("data", dictService.findByType(parentCode));
		return result;
	}
	
	@PostMapping(value="/dict/update", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> update(Dict dict) {
		Map<String, String> result = new HashMap<String, String>();
		boolean success = true;
		try{
			dictService.update(dict);
		} catch (Exception e) {
			result.put("error", e.getMessage());
			success = false;
			
		}
		result.put("success", String.valueOf(success));
		return result;
	}
	
	@PostMapping(value="/dict/addDic", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> addDic(Dict dict) {
		dict.setParentCode("-1");
		return addDicData(dict);
	}
	
	@PostMapping(value="/dict/addDicData", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> addDicData(Dict dict) {
		Map<String, String> result = new HashMap<String, String>();
		boolean success = true;
		try{
			dictService.save(dict);
		} catch (Exception e) {
			result.put("error", e.getMessage());
			success = false;
		}
		result.put("success", String.valueOf(success));
		return result;
	}
	
	@PostMapping(value="/dict/delete", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResult delete(Dict dict) {
		JsonResult json = new JsonResult();
		return json.setSuccess(dictService.delete(dict) == 1);
	}
	
	
	
	@GetMapping(value="/dict/validate/code", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> validateCode(Dict dict) {
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			Dict newDict = dictService.findByCodeAndParentCode(dict.getCode(), dict.getParentCode());
			if(newDict != null) {
				result.put("validate", false);		
			} else {
				result.put("validate", true);
			}
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}
		return result;
	}
	
}
