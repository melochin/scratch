package scratch.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ListenerService {


	private BlockingQueue<Listener> listeners;

	public void addListener(Listener listener) {
		if(listeners == null) {
			listeners = new LinkedBlockingQueue<Listener>();
		}
		listeners.add(listener);
		System.out.println("listeners size" + listeners.size());
	}

	public void handle(Set data) {
		listeners.stream().forEach(listener -> listener.handle(data));
	}

}
