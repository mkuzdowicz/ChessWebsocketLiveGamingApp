package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.ChessMoveDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class ChessMoveMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(ChessMoveMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageDto,
			LiveGamingContextAdapter gamingCtxAdapter) {

		LiveGamingUsersRegistry liveGamingUsersRepository = gamingCtxAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRegistry webSocketSessionsRepository = gamingCtxAdapter.getWebSocketSessionsRepository();
		LiveChessGamesRegistry liveChessGamesRepository = gamingCtxAdapter.getLiveChessGamesRepository();

		String fromUsername = messageDto.getSendFrom();
		LiveGamingUserDto fromUser = liveGamingUsersRepository.getWebsocketUser(fromUsername);

		String toUsername = messageDto.getSendTo();
		LiveGamingUserDto toUser = liveGamingUsersRepository.getWebsocketUser(toUsername);

		if (toUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

			if (userONEPlayWithUserTWO(fromUser, toUser)) {

				ChessMoveDto currentMove = messageDto.getChessMove();
				liveChessGamesRepository.addActualMoveToThisGameObject(toUser.getUniqueActualGameHash(), currentMove);
				liveChessGamesRepository.incrementNumberOfMoves(toUser.getUniqueActualGameHash());
				webSocketSessionsRepository.sendMessageToOneUser(messageDto);

			} else {
				logger.debug(messageDto.getSendFrom()
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
