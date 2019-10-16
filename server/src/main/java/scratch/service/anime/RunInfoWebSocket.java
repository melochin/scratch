package scratch.service.anime;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import scratch.model.view.RunInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RunInfoWebSocket extends TextWebSocketHandler {

	private static final List<WebSocketSession> users = new ArrayList<>();

	@Autowired
	private AnimeScratchService animeScratchService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println(session.getId() + "建立连接");
		sendMessage(animeScratchService.getRunMessage(), session);
		users.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println(session.getId() + "销毁连接");
		users.remove(session);
	}

	public void sendMessage(RunInfo runInfo) {
		for(WebSocketSession user : users) {
			sendMessage(runInfo, user);
		}
	}

	public void sendMessage(RunInfo runInfo, WebSocketSession user) {
		String json = new Gson().toJson(runInfo);
		try {
			user.sendMessage(new TextMessage(json.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}