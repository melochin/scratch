package scratch.service;

import reactor.core.publisher.Flux;

public interface Listener<T> {

	void beforeHandle();

	void handle(T data);

}
