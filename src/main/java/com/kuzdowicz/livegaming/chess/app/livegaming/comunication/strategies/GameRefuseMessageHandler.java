package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class GameRefuseMessageHandler implements GameMessagesHandler {

	@Override
	public synchronized void reactToMessages(GameMessageDto messageDto,
			LiveGamingContextAdapter gamingCtxAdapter) {

		WebSocketSessionsRegistry webSocketSessionsRepository = gamingCtxAdapter.getWebSocketSessionsRepository();
		LiveGamingUsersRegistry liveGamingUsersRepository = gamingCtxAdapter.getLiveGamingUsersRepository();

		webSocketSessionsRepository.sendMessageToOneUser(messageDto);
		
		liveGamingUsersRepository.resetPlayersPairStateToWiatForNewGame(messageDto);
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

}
