package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuzdowicz.livegaming.chess.app.constants.ChessMoveStatus;
import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveGamingUsersRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

public class GameInvitationAgreementMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(GameInvitationAgreementMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageObj,
			RepositoriesAdapterForLiveGaming repositoriesAdapter) {
		logger.debug("reactToMessages()");

		LiveGamingUsersRepository liveGamingUsersRepository = repositoriesAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRepository webSocketSessionsRepository = repositoriesAdapter.getWebSocketSessionsRepository();
		LiveChessGamesRepository liveChessGamesRepository = repositoriesAdapter.getLiveChessGamesRepository();

		String actualChessGameUUID = UUID.randomUUID().toString();

		liveGamingUsersRepository.setComStatusIsPlaying(messageObj.getSendTo(), messageObj.getSendFrom());
		liveGamingUsersRepository.setComStatusIsPlaying(messageObj.getSendFrom(), messageObj.getSendTo());

		LiveGamingUserDto sendToObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());

		sendToObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendToObj(sendToObj);

		LiveGamingUserDto sendFromObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendFrom());

		sendFromObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendFromObj(sendFromObj);
		messageObj.setMoveStatus(ChessMoveStatus.WHITE_TO_MOVE);

		ChessGame newChessGame = ChessGame.prepareAndReturnChessGameObjectAtGameStart(actualChessGameUUID, sendToObj,
				sendFromObj, messageObj);

		liveChessGamesRepository.addNewGame(newChessGame);
		webSocketSessionsRepository.sendMessageToOneUser(messageObj);
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

}
