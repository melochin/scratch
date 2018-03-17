package scratch.service;

public interface Handler<T> {

	void handle(T data);

}
