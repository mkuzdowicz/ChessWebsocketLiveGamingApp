package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveGamingUsersRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

public class GameInvitationMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(GameInvitationMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageObj, RepositoriesAdapterForLiveGaming repositoriesAdapter) {

		LiveGamingUsersRepository liveGamingUsersRepository = repositoriesAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRepository webSocketSessionsRepository = repositoriesAdapter.getWebSocketSessionsRepository();

		LiveGamingUserDto invitedUser = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());

		if (invitedUser != null
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_DURING_HANDSHAKE)
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

			liveGamingUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendFrom());
			liveGamingUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendTo());
			liveGamingUsersRepository.setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());

			LiveGamingUserDto sendToObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());
			messageObj.setSendToObj(sendToObj);

			LiveGamingUserDto sendFromObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendFrom());
			messageObj.setSendFromObj(sendFromObj);

			webSocketSessionsRepository.sendMessageToOneUser(messageObj);
			webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
			
		} else {
			logger.debug("invited user is already playing, is during handshake or is null");
			GameMessageDto tryLaterMsg = new GameMessageDto();
			tryLaterMsg.setType(GameMessageType.TRY_LATER);
			webSocketSessionsRepository.sendToSession(messageObj.getSendFrom(), "server",
					new GsonBuilder().create().toJson(tryLaterMsg));
		}

	}

}
