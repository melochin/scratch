package scratch.controller.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import scratch.dao.redis.ICardRepository;
import scratch.model.entity.Card;
import scratch.model.entity.MemoryCardInfo;
import scratch.model.view.CardDisplay;
import scratch.service.Listener;
import scratch.service.ListenerService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ApiCardController {

	@Autowired
	private ICardRepository cardRepository;

	@Autowired
	private ListenerService listenerService;

	@PostMapping("/api/brochures/{brochureId}/cards")
	public void save(@PathVariable("brochureId") String brochureId, @RequestBody Card card) {
		cardRepository.save(brochureId, card);
		return;
	}

	@PostMapping("/api/brochures/{brochureId}/cards/list")
	public void saveList(@PathVariable("brochureId") String brochureId, @RequestBody Card[] cards) {
		for(Card card : cards) {
			cardRepository.save(brochureId, card);
		}
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
	public List<CardDisplay> list(@PathVariable("brochureId") String brochureId) {
		return cardRepository.list(brochureId).stream()
				.map(card -> this.wrapCardDisplay(brochureId, card))
				.collect(Collectors.toList());
	}

	@GetMapping(value="/api/cards/{word}")
	public List<CardDisplay> listByWord(@PathVariable("word") String word) {
		return cardRepository.listByWord(word).stream()
				.map(card -> new CardDisplay(card))
				.collect(Collectors.toList());
	}

	private CardDisplay wrapCardDisplay(String brochureId, Card card) {
		MemoryCardInfo memoryCardInfo = cardRepository.findMemoryCardInfo(brochureId, card.getId());
		if(memoryCardInfo == null) memoryCardInfo = new MemoryCardInfo();
		return new CardDisplay(card, memoryCardInfo);
	};


	@GetMapping(value="/api/brochures/{brochureId}/cards/memory")
	public List<Card> memory(@PathVariable("brochureId") String brochureId) {
		return cardRepository.listMemory(brochureId);
	}

	@PutMapping("/api/brochures/{brochureId}/cards/memory/rember")
	public void memoryRember(@PathVariable("brochureId") String brochureId, @RequestBody Card card) {
		cardRepository.memoryRember(brochureId, card.getId());
	}

	@PutMapping("/api/brochures/{brochureId}/cards/memory/forget")
	public void memoryForget(@PathVariable("brochureId") String brochureId, @RequestBody Card card) {
		cardRepository.memoryForget(brochureId, card.getId());
	}

	@PutMapping("/api/brochures/{brochureId}/cards/swap/{firstId}/{secondId}")
	public void swap(@PathVariable("brochureId") String brochureId,
					 @PathVariable("firstId") String firstId,
					 @PathVariable("secondId") String secondId) {
		cardRepository.swap(brochureId, firstId, secondId);
	}

	@PutMapping("/api/brochures/{brochureId}/cards/swap/before/{firstId}/{secondId}")
	public void swapBefore(@PathVariable("brochureId") String brochureId,
					 @PathVariable("firstId") String firstId,
					 @PathVariable("secondId") String secondId) {
		cardRepository.swapBefore(brochureId, firstId, secondId);
	}




	@DeleteMapping("/api/brochures/{brochureId}/cards")
	public void delete(@PathVariable("brochureId") String brochureId,
							@RequestBody Card card) {
		cardRepository.delete(brochureId, card);
		return;
	}

}
