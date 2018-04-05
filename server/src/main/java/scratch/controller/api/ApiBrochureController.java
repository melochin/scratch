package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import scratch.dao.BrochureRepository;
import scratch.model.RedisKey;
import scratch.model.entity.Brochure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ApiBrochureController {

	@Autowired
	private BrochureRepository repository;

	@GetMapping("/api/brochures")
	public List<Brochure> list() {
		return repository.list();
	}

	@PostMapping("/api/brochures")
	public void save(@RequestBody Brochure brochure) {
		repository.save(brochure);
	}

	@PutMapping("/api/brochures")
	public void modify(@RequestBody Brochure brochure) {
		repository.modify(brochure);
	}

	@DeleteMapping("/api/brochures/{brochureId}")
	public void delete(@PathVariable("brochureId") String brochureId) {
		repository.delete(brochureId);
	}

}
