package scratch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.model.Dict;
import scratch.service.DictService;

@Controller
public class DictController {

	@Autowired
	private DictService dictService;
	
	@GetMapping("/dic")
	public String index(Model model) {
		model.addAttribute("dictionaries", dictService.findAllDictionaries());
		return "dic_index";
	}
	
	@GetMapping(value="/dic/parentcode/{parentCode}", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Dict> getDicts(@PathVariable("parentCode") String parentCode) {
		return dictService.findByType(parentCode);
	}
	
}
