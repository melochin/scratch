package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import scratch.dao.BrochureRepository;
import scratch.dao.CardRepository;
import scratch.model.RedisKey;
import scratch.model.entity.Brochure;
import scratch.model.entity.Card;
import scratch.service.Handler;
import scratch.service.Listener;
import scratch.service.ListenerService;
import scratch.service.RedisService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ApiCardController {

	@Autowired
	private RedisTemplate redisTemplate;

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
					new Listener<Set>(name, data -> fluxSink.next(data)));
		});
	}

	@GetMapping(value="/api/brochures/{brochureId}/cards")
	public Map list(@PathVariable("brochureId") String brochureId) {
		Set<Card> cards = cardRepository.list(brochureId);
		Brochure brochure = brochureRepository.find(brochureId);

		Map result = new HashMap();
		result.put("brochure", brochure);
		result.put("cards", cards);
		return result;
	}

	@DeleteMapping("/api/brochures/{brochureId}/cards")
	public void delete(@PathVariable("brochureId") String brochureId,
							@RequestBody Card card) {
		cardRepository.delete(brochureId, card);
		return;
	}

}
