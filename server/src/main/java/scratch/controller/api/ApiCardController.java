package scratch.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import scratch.model.entity.Card;
import scratch.service.Listener;
import scratch.service.ListenerService;
import scratch.service.RedisService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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

	@GetMapping(value="/api/stream/cards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Object> streamList() {
		return Flux.create(fluxSink -> {

			fluxSink.next(redisService.list("card"));

			listenerService.addListener(new Listener<Set>() {

				@Override
				public void beforeHandle() {

				}

				@Override
				public void handle(Set data) {
					fluxSink.next(data);
					fluxSink.complete();
				}
			});
		// 长时间没有收到新数据，那么得让fluxsink完成，且取消该监听器
		}).doOnNext((s) -> System.out.println("get data"));
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
