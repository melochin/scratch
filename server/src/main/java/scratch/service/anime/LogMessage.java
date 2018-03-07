package scratch.service.anime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogMessage {

	private static BlockingQueue<String> messages;

	static {
		messages = new LinkedBlockingQueue<String>();
	}

	public static void add(String message) {
		messages.add(message);
	}

	public static void clear() {
		messages.clear();
	}

	public static BlockingQueue<String> get() {
		return messages;
	}

}
