package scratch.controller.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.dao.redis.BrochureRepository;
import scratch.model.entity.Brochure;

import java.util.List;

@RestController
public class ApiBrochureController {

	@Autowired
	private BrochureRepository brochureRepository;

	@GetMapping("/api/brochures")
	public List<Brochure> list() {
		return brochureRepository.list();
	}

	@PostMapping("/api/brochures")
	public Brochure save(@RequestBody Brochure brochure) {
		return brochureRepository.save(brochure);
	}

	@PutMapping("/api/brochures")
	public Brochure modify(@RequestBody Brochure brochure) {
		return brochureRepository.modify(brochure);
	}

	@DeleteMapping("/api/brochures/{brochureId}")
	public Brochure delete(@PathVariable("brochureId") String brochureId) {
		return brochureRepository.delete(brochureId);
	}

}
