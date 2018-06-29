package scratch.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ListenerService {

	private BlockingQueue<Listener> listeners = new LinkedBlockingQueue<>();

	public void addListener(Listener listener) {
		if(listeners == null) {
			listeners = new LinkedBlockingQueue<Listener>();
		}
		listeners.add(listener);
		System.out.println("listeners size" + listeners.size());
	}

	public void handle(List data) {
		listeners.stream().forEach(listener -> {
			listener.handle(data);
		});
	}

	public void remove(String sessionId) {
		listeners.stream().forEach(listener -> {
			if(listener.getName().equals(sessionId)) {
				listeners.remove(listener);
				System.out.println(listeners.size());
			}
		});
	}

}
