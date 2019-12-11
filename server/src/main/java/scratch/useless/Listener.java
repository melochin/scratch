package scratch.useless;

@Deprecated
public class Listener<T> {

	private String name;

	private Handler<T> handler;

	public Listener(String name, Handler<T> handler) {
		this.name = name;
		this.handler = handler;
	}

	public void handle(T data) {
		this.handler.handle(data);
	}

	public String getName() {
		return name;
	}

	public Handler<T> getHandler() {
		return handler;
	}

}
