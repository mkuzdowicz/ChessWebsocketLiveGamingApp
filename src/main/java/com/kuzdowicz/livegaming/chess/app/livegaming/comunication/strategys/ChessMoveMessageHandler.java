package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.ChessMoveDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveGamingUsersRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

public class ChessMoveMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(ChessMoveMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageObj,
			RepositoriesAdapterForLiveGaming repositoriesAdapter) {

		LiveGamingUsersRepository liveGamingUsersRepository = repositoriesAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRepository webSocketSessionsRepository = repositoriesAdapter.getWebSocketSessionsRepository();
		LiveChessGamesRepository liveChessGamesRepository = repositoriesAdapter.getLiveChessGamesRepository();

		String fromUsername = messageObj.getSendFrom();
		LiveGamingUserDto fromUser = liveGamingUsersRepository.getWebsocketUser(fromUsername);

		String toUsername = messageObj.getSendTo();
		LiveGamingUserDto toUser = liveGamingUsersRepository.getWebsocketUser(toUsername);

		if (toUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

			if (userONEPlayWithUserTWO(fromUser, toUser)) {

				ChessMoveDto currentMove = messageObj.getChessMove();
				liveChessGamesRepository.addActualMoveToThisGameObject(toUser.getUniqueActualGameHash(), currentMove);
				liveChessGamesRepository.incrementNumberOfMoves(toUser.getUniqueActualGameHash());
				webSocketSessionsRepository.sendMessageToOneUser(messageObj);

			} else {
				logger.debug(messageObj.getSendFrom()
						+ " send message to user with which he is not playing , ( to user: " + toUsername + " )");
			}
		}

	}

	private boolean userONEPlayWithUserTWO(LiveGamingUserDto fromUser, LiveGamingUserDto toUser) {
		if (fromUser != null && toUser != null && fromUser.getPlayNowWithUser().equals(toUser.getUsername())
				&& toUser.getPlayNowWithUser().equals(fromUser.getUsername())) {
			return true;
		} else {
			return false;
		}
	}

}
