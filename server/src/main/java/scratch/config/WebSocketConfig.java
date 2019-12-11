package scratch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import scratch.service.anime.RunInfoWebSocket;

/**
 * 配置WebSocket处理
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private RunInfoWebSocket runInfoWebSocket;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry
				.addHandler(runInfoWebSocket, "/api/admin/scratch/runInfo")
				.setAllowedOrigins("*")
				.addInterceptors(new HttpSessionHandshakeInterceptor());
	}

}


