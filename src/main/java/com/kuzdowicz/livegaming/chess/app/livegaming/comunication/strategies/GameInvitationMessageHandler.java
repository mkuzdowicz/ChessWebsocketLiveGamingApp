package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class GameInvitationMessageHandler implements GameMessagesHandler {

	private final static Logger logger = LoggerFactory.getLogger(GameInvitationMessageHandler.class);

	@Override
	public synchronized void reactToMessages(GameMessageDto messageDto, LiveGamingContextAdapter gamingCtxAdapter) {

		LiveGamingUsersRegistry liveGamingUsersRegistry = gamingCtxAdapter.getLiveGamingUsersRegistry();
		WebSocketSessionsRegistry webSocketSessionsRegistry = gamingCtxAdapter.getWebSocketSessionsRegistry();

		LiveGamingUserDto invitedUser = liveGamingUsersRegistry.getWebsocketUser(messageDto.getSendTo());

		if (invitedUser != null && invitedUser.isInReadyForInvitationState()) {

			liveGamingUsersRegistry.setPlayersPairInInvitationState(messageDto);

			webSocketSessionsRegistry.sendMessageToOneUser(messageDto);
			webSocketSessionsRegistry.sendToAllConnectedSessionsActualParticipantList();

		} else {
			logger.debug("invited user is already playing, is during handshake or is null");
			GameMessageDto tryLaterMsg = new GameMessageDto();
			tryLaterMsg.setType(GameMessageType.TRY_LATER);
			webSocketSessionsRegistry.sendToSession(messageDto.getSendFrom(), "server",
					new GsonBuilder().create().toJson(tryLaterMsg));
		}

	}

}
