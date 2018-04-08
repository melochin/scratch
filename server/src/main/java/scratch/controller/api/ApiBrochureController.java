package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scratch.dao.BrochureRepository;
import scratch.dao.CardRepository;
import scratch.model.entity.Brochure;

import java.util.List;
import java.util.Optional;

@RestController
public class ApiBrochureController {

	@Autowired
	private BrochureRepository brochureRepository;

	@Autowired
	private CardRepository cardRepository;

	@GetMapping("/api/brochures")
	public List<Brochure> list() {
		return brochureRepository.list();
	}

	@PostMapping("/api/brochures")
	public void save(@RequestBody Brochure brochure) {
		brochureRepository.save(brochure);
	}

	@PutMapping("/api/brochures")
	public void modify(@RequestBody Brochure brochure) {
		brochureRepository.modify(brochure);
	}

	@DeleteMapping("/api/brochures/{brochureId}")
	public void delete(@PathVariable("brochureId") String brochureId) {
		brochureRepository.delete(brochureId);
	}

}
