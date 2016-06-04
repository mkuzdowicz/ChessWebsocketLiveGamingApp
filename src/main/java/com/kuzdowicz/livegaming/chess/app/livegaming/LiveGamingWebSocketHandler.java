package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.models.GameMessage;
import com.kuzdowicz.livegaming.chess.app.models.GameUser;

@Component
public class LiveGamingWebSocketHandler extends TextWebSocketHandler {

	private final static Logger log = Logger.getLogger(LiveGamingWebSocketHandler.class);

	private final WebSocketSessionsRepository webSocketSessionsRepository = new WebSocketSessionsRepository();
	private final GameUsersRepository usesrHandler = new GameUsersRepository();
	private final LiveChessGamesRepository chessGamesHandler = new LiveChessGamesRepository();

	private GameMessageProtocol gameMessageProtocol = new GameMessageProtocol(webSocketSessionsRepository, usesrHandler,
			chessGamesHandler);

	@Autowired
	private Gson gson;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		log.debug("afterConnectionEstablished()");

		String connectionUriPath = session.getUri().getPath();
		String[] connUriSpliietdBySlash = connectionUriPath.split("/");
		String sender = connUriSpliietdBySlash[connUriSpliietdBySlash.length - 1];

		if (usesrHandler.userListNotContainsUsername(sender)) {

			GameUser gameUser = new GameUser(sender);
			gameUser.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

			webSocketSessionsRepository.addSession(sender, session);
			usesrHandler.addWebsocketUser(gameUser);
			webSocketSessionsRepository.sendToAllConnectedSessions(gameUser.getUsername());
		}

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		String msg = message.getPayload();
		GameMessage gameMessage = gson.fromJson(msg, GameMessage.class);
		gameMessageProtocol.proccessMessage(gameMessage, msg);

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("afterConnectionClosed()");

		String sender = session.getAttributes().get("username").toString();

		synchronized (this) {
			GameUser cloesingConnectionUser = usesrHandler.getWebsocketUser(sender);

			if (cloesingConnectionUser.getPlayNowWithUser() != null
					&& cloesingConnectionUser.getPlayNowWithUser() != "") {

				GameUser cloesingConnectionUserGamePartner = usesrHandler
						.getWebsocketUser(cloesingConnectionUser.getPlayNowWithUser());

				cloesingConnectionUserGamePartner.setPlayNowWithUser(null);
				cloesingConnectionUserGamePartner.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);

				GameMessage disconnectMsg = new GameMessage();
				disconnectMsg.setType(GameMessageType.USER_DISCONNECT);

				webSocketSessionsRepository.sendToSession(cloesingConnectionUserGamePartner.getUsername(), sender,
						gson.toJson(disconnectMsg));
			}

			usesrHandler.removeWebsocketUser(sender);
			webSocketSessionsRepository.removeSession(sender);
		}
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.debug("there was an error with connection");
		log.warn(exception);
	}

}
