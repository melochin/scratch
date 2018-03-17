package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import scratch.model.entity.Card;
import scratch.service.Handler;
import scratch.service.Listener;
import scratch.service.ListenerService;
import scratch.service.RedisService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RestController
public class ApiCardController {

	@Autowired
	private RedisService redisService;

	@Autowired
	private ListenerService listenerService;

	@PostMapping("/api/cards")
	public Set<Card> save(@RequestBody Card card) {
		card.generateId();
		redisService.add("card", card);
		listenerService.handle(redisService.list("card"));
		return redisService.list("card");
	}

	@GetMapping(value = "/api/stream/cards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Object> streamList(HttpServletRequest request) {
		String name = request.getSession().getId();
		return Flux.create(fluxSink -> {
			fluxSink.next(redisService.list("card"));
			listenerService.addListener(
					new Listener<Set>(name, data -> fluxSink.next(data)));
		});
	}

	@GetMapping(value="/api/cards")
	public Set list() {
		return redisService.list("card");
	}

	@DeleteMapping("/api/cards")
	public Set<Card> delete(@RequestBody Card card) {
		redisService.pop("card", card);
		listenerService.handle(redisService.list("card"));
		return redisService.list("card");
	}

}
