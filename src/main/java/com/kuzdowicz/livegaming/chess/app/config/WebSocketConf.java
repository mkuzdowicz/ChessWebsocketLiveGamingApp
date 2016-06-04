package com.kuzdowicz.livegaming.chess.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kuzdowicz.livegaming.chess.app.websockets.MyWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConf implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(webSocketHandler(), "/chessapp-live-game/{sender}");

	}

	@Bean
	MyWebSocketHandler webSocketHandler() {
		return new MyWebSocketHandler();
	}
}
