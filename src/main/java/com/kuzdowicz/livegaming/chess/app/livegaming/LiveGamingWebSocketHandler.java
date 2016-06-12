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

@Component
public class LiveGamingWebSocketHandler extends TextWebSocketHandler {

	private final static Logger log = LoggerFactory.getLogger(LiveGamingWebSocketHandler.class);

	private final WebSocketSessionsRepository webSocketSessionsRepository;
	private final LiveGamingUsersRepository liveGamingUsersRepository;
	private final GameMessageProtocolService gameMessageProtocol;
	private final Gson gson;

	@Autowired
	public LiveGamingWebSocketHandler(WebSocketSessionsRepository webSocketSessionsRepository,
			LiveGamingUsersRepository liveGamingUsersRepository, GameMessageProtocolService gameMessageProtocol,
			Gson gson) {
		this.webSocketSessionsRepository = webSocketSessionsRepository;
		this.liveGamingUsersRepository = liveGamingUsersRepository;
		this.gameMessageProtocol = gameMessageProtocol;
		this.gson = gson;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String connectionUriPath = session.getUri().getPath();
		String[] connUriSpliietdBySlash = connectionUriPath.split("/");
		String sender = connUriSpliietdBySlash[connUriSpliietdBySlash.length - 1];

		if (liveGamingUsersRepository.userListNotContainsUsername(sender)) {

			LiveGamingUserDto gameUser = new LiveGamingUserDto(sender);
			gameUser.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

			webSocketSessionsRepository.addSession(sender, session);
			liveGamingUsersRepository.addWebsocketUser(gameUser);
			webSocketSessionsRepository.sendToAllConnectedSessions(gameUser.getUsername());
		}

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload();
		GameMessageDto gameMessage = gson.fromJson(msg, GameMessageDto.class);
		gameMessageProtocol.proccessMessage(gameMessage, msg);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		String sender = session.getAttributes().get("username").toString();
		synchronized (this) {
			LiveGamingUserDto cloesingConnectionUser = liveGamingUsersRepository.getWebsocketUser(sender);

			if (cloesingConnectionUser.getPlayNowWithUser() != null
					&& cloesingConnectionUser.getPlayNowWithUser() != "") {

				LiveGamingUserDto cloesingConnectionUserGamePartner = liveGamingUsersRepository
						.getWebsocketUser(cloesingConnectionUser.getPlayNowWithUser());

				cloesingConnectionUserGamePartner.setPlayNowWithUser(null);
				cloesingConnectionUserGamePartner.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

				GameMessageDto disconnectMsg = new GameMessageDto();
				disconnectMsg.setType(GameMessageType.USER_DISCONNECT);

				webSocketSessionsRepository.sendToSession(cloesingConnectionUserGamePartner.getUsername(), sender,
						gson.toJson(disconnectMsg));
			}

			liveGamingUsersRepository.removeWebsocketUser(sender);
			webSocketSessionsRepository.removeSession(sender);
		}
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.warn("there was an error with connection: ", exception);
	}

}
