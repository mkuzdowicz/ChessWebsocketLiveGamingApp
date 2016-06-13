package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import java.util.HashMap;
import java.util.Map;

import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;

public class GameMessageHandlerFacotry {

	private final static Map<String, GameMessagesHandler> messageTypeToHandlerMap = new HashMap<>();

	static {
		messageTypeToHandlerMap.put(GameMessageType.USER_CONNECT, new NewParticipantConnectedMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.GAME_HANDSHAKE_INVITATION, new GameInvitationMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.GAME_HANDSHAKE_AGREEMENT,
				new GameInvitationAgreementMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.GAME_HANDSHAKE_REFUSE, new GameRefuseMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.CHESS_MOVE, new ChessMoveMessageHandler());

		messageTypeToHandlerMap.put(GameMessageType.GAME_OVER, new GameEndMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.USER_DISCONNECT, new GameEndMessageHandler());
		messageTypeToHandlerMap.put(GameMessageType.QUIT_GAME, new GameEndMessageHandler());
	}

	public GameMessagesHandler createMessageHandler(String gameMessageType) {
		return messageTypeToHandlerMap.get(gameMessageType);
	}

}
