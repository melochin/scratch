package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scratch.exception.NotFoundException;
import scratch.model.entity.Dict;
import scratch.service.DictService;
import scratch.support.web.JsonResult;

import java.util.List;
import java.util.Map;

@RestController
public class ApiDictController {

	@Autowired
	private DictService dictService;

	@GetMapping(value = "/api/admin/dics", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Dict> list(
			@RequestParam(value = "parentCode", required = false) String parentCode) {
		List<Dict> dicts;
		System.out.println(parentCode);
		if (parentCode == null) {
			dicts = dictService.findAllDictionaries();
		} else {
			dicts = dictService.findByParentCode(parentCode);
		}
		return dicts;
	}

	@PostMapping(value = "/api/admin/dics", produces = MediaType.APPLICATION_JSON_VALUE)
	public Dict save(@RequestBody Dict dict) {
		dictService.save(dict);
		return dict;
	}

	@PutMapping(value = "/api/admin/dics", produces = MediaType.APPLICATION_JSON_VALUE)
	public Dict update(@RequestBody Dict dict) {
		// 更新失败抛出异常
		if (dictService.update(dict) == 0) {
			throw new NotFoundException();
		}
		return dictService.findByCodeAndParentCode(dict.getCode(),
				dict.getParentCode());
	}

	@DeleteMapping(value = "/api/admin/dics/{parentCode}/{code}")
	public void delete(
			@PathVariable(value = "parentCode", required = true) String parentCode,
			@PathVariable(value = "code", required = true) String code) {
		Dict dict = new Dict();
		dict.setParentCode(parentCode);
		dict.setCode(code);
		dictService.delete(dict);
		return;
	}

	@GetMapping(value = "/api/admin/dics/validate/{code}/{parentCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult validateCode(
			@PathVariable(value = "code", required = true) String code,
			@PathVariable(value = "parentCode", required = true) String parentCode) {
		JsonResult result = new JsonResult();
		Dict newDict = dictService.findByCodeAndParentCode(code, parentCode);
		result.setValidate(newDict == null);
		return result;
	}
}
