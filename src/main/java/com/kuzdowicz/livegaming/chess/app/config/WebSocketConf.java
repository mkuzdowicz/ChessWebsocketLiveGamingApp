package com.kuzdowicz.livegaming.chess.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConf implements WebSocketConfigurer {

	@Autowired
	private LiveGamingWebSocketHandler liveGamingWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(liveGamingWebSocketHandler, "/chessapp-live-game/{sender}");

	}

}
