package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scratch.service.anime.AnimeScratchService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiScratchInfoController {

	@Autowired
	private AnimeScratchService scratchService;

	@GetMapping(value = "/api/admin/scratch/runstatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map getRunstatus() {
		Map map = new HashMap();
		map.put("status", scratchService.isRun());
		return map;
	}

	@GetMapping(value = "/api/admin/scratch/runmessage", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map getRunMessage() {
		Map map = new HashMap();
		map.put("status", scratchService.isRun());
		map.put("messages", scratchService.getMessages());
		return map;
	}

	@GetMapping(value = "/api/admin/scratch/run", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map run() {
		Map map = new HashMap();
		scratchService.run();
		map.put("status", scratchService.isRun());
		return map;
	}

}
