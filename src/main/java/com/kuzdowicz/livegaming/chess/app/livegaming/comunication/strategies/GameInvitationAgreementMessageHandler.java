package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuzdowicz.livegaming.chess.app.constants.ChessMoveStatus;
import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class GameInvitationAgreementMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(GameInvitationAgreementMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageDto,
			LiveGamingContextAdapter gamingCtxAdapter) {
		logger.debug("reactToMessages()");

		LiveGamingUsersRegistry liveGamingUsersRepository = gamingCtxAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRegistry webSocketSessionsRepository = gamingCtxAdapter.getWebSocketSessionsRepository();
		LiveChessGamesRegistry liveChessGamesRepository = gamingCtxAdapter.getLiveChessGamesRepository();

		String actualChessGameUUID = UUID.randomUUID().toString();

		liveGamingUsersRepository.setComStatusIsPlaying(messageDto.getSendTo(), messageDto.getSendFrom());
		liveGamingUsersRepository.setComStatusIsPlaying(messageDto.getSendFrom(), messageDto.getSendTo());

		LiveGamingUserDto sendToObj = liveGamingUsersRepository.getWebsocketUser(messageDto.getSendTo());

		sendToObj.setUniqueActualGameHash(actualChessGameUUID);
		messageDto.setSendToObj(sendToObj);

		LiveGamingUserDto sendFromObj = liveGamingUsersRepository.getWebsocketUser(messageDto.getSendFrom());

		sendFromObj.setUniqueActualGameHash(actualChessGameUUID);
		messageDto.setSendFromObj(sendFromObj);
		messageDto.setMoveStatus(ChessMoveStatus.WHITE_TO_MOVE);

		ChessGame newChessGame = ChessGame.prepareAndReturnChessGameObjectAtGameStart(actualChessGameUUID, sendToObj,
				sendFromObj, messageDto);

		liveChessGamesRepository.addNewGame(newChessGame);
		webSocketSessionsRepository.sendMessageToOneUser(messageDto);
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

}
