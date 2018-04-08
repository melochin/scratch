package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import scratch.dao.BrochureRepository;
import scratch.dao.CardRepository;
import scratch.model.entity.Card;
import scratch.service.Listener;
import scratch.service.ListenerService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class ApiCardController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private BrochureRepository brochureRepository;


	@Autowired
	private ListenerService listenerService;

	@PostMapping("/api/brochures/{brochureId}/cards")
	public void save(@PathVariable("brochureId") String brochureId, @RequestBody Card card) {
		cardRepository.save(brochureId, card);
		return;
	}

	@GetMapping(value = "/api/stream/brochures/{brochureId}/cards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Object> streamList(@PathVariable("brochureId") String brochureId,
								   HttpServletRequest request) {
		String name = request.getSession().getId();
		return Flux.create(fluxSink -> {
			fluxSink.next(cardRepository.list(brochureId));
			listenerService.addListener(
					new Listener<List>(name, data -> fluxSink.next(data)));
		});
	}

	@GetMapping(value="/api/brochures/{brochureId}/cards")
	public List<Card> list(@PathVariable("brochureId") String brochureId) {
		return cardRepository.list(brochureId);
	}

	@GetMapping(value="/api/brochures/{brochureId}/cards/memory")
	public List<Card> memory(@PathVariable("brochureId") String brochureId) {

		Integer size = brochureRepository.findMemory(brochureId);
		if(size == null) {
			return cardRepository.list(brochureId);
		}

		return cardRepository.list(brochureId, size-1);
	}

	@PutMapping("/api/brochures/{brochureId}/cards/memory")
	public void modifyMemory(@PathVariable("brochureId") String brochureId, @RequestBody Card card) {
		Integer memory = Optional.ofNullable(brochureRepository.findMemory(brochureId))
				.orElse(cardRepository.count(brochureId));
		brochureRepository.modifyMemory(brochureId, memory-1);
		cardRepository.deleteAndSave(brochureId, card);
	}


	@DeleteMapping("/api/brochures/{brochureId}/cards")
	public void delete(@PathVariable("brochureId") String brochureId,
							@RequestBody Card card) {
		cardRepository.delete(brochureId, card);
		return;
	}

}
