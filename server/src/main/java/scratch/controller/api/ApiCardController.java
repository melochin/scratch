package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import scratch.model.entity.Card;
import scratch.service.RedisService;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RestController
public class ApiCardController {

	@Autowired
	private RedisService redisService;

	@PostMapping("/api/cards")
	public Set<Card> save(@RequestBody Card card) {
		card.generateId();
		redisService.add("card", card);
		return redisService.list("card");
	}

	@GetMapping(value="/api/stream/cards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Set> streamList() {
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(5));
		Flux<Set> events = Flux.fromStream(
				Stream.generate(()-> redisService.list("card")));
		return Flux.zip(interval, events).map(Tuple2::getT2);
	}

	@GetMapping(value="/api/cards")
	public Set list() {
		return redisService.list("card");
	}

	@DeleteMapping("/api/cards")
	public Set<Card> delete(@RequestBody Card card) {
		redisService.pop("card", card);
		return redisService.list("card");
	}

}
