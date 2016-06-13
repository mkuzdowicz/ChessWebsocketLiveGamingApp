package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

@Component
public class LiveGamingWebSocketHandler extends TextWebSocketHandler {

	private final static Logger log = LoggerFactory.getLogger(LiveGamingWebSocketHandler.class);

	private final WebSocketSessionsRegistry webSocketSessionsRegistry;
	private final LiveGamingUsersRegistry liveGamingUsersRegistry;
	private final LiveGamingMessageSendingHandler gameMessageHandler;
	private final Gson gson;

	@Autowired
	public LiveGamingWebSocketHandler(WebSocketSessionsRegistry webSocketSessionsRegistry,
			LiveGamingUsersRegistry liveGamingUsersRegistry, LiveGamingMessageSendingHandler gameMessageHandler,
			Gson gson) {
		this.webSocketSessionsRegistry = webSocketSessionsRegistry;
		this.liveGamingUsersRegistry = liveGamingUsersRegistry;
		this.gameMessageHandler = gameMessageHandler;
		this.gson = gson;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String connectionUriPath = session.getUri().getPath();
		String[] connUriSpliietdBySlash = connectionUriPath.split("/");
		String sender = connUriSpliietdBySlash[connUriSpliietdBySlash.length - 1];

		if (liveGamingUsersRegistry.userListNotContainsUsername(sender)) {

			LiveGamingUserDto gameUser = new LiveGamingUserDto(sender);
			gameUser.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

			webSocketSessionsRegistry.addSession(sender, session);
			liveGamingUsersRegistry.addWebsocketUser(gameUser);
			webSocketSessionsRegistry.sendToAllConnectedSessions(gameUser.getUsername());
		}

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		GameMessageDto gameMessage = gson.fromJson(message.getPayload(), GameMessageDto.class);
		gameMessageHandler.proccessMessage(gameMessage);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		String sender = session.getAttributes().get("username").toString();
		synchronized (this) {
			LiveGamingUserDto cloesingConnectionUser = liveGamingUsersRegistry.getWebsocketUser(sender);

			if (cloesingConnectionUser.getPlayNowWithUser() != null
					&& cloesingConnectionUser.getPlayNowWithUser() != "") {

				LiveGamingUserDto cloesingConnectionUserGamePartner = liveGamingUsersRegistry
						.getWebsocketUser(cloesingConnectionUser.getPlayNowWithUser());

				cloesingConnectionUserGamePartner.setPlayNowWithUser(null);
				cloesingConnectionUserGamePartner.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

				GameMessageDto disconnectMsg = new GameMessageDto();
				disconnectMsg.setType(GameMessageType.USER_DISCONNECT);

				webSocketSessionsRegistry.sendToSession(cloesingConnectionUserGamePartner.getUsername(), sender,
						gson.toJson(disconnectMsg));
			}

			liveGamingUsersRegistry.removeWebsocketUser(sender);
			webSocketSessionsRegistry.removeSession(sender);
		}
		webSocketSessionsRegistry.sendToAllConnectedSessionsActualParticipantList();
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.warn("there was an error with connection: ", exception);
	}

}
