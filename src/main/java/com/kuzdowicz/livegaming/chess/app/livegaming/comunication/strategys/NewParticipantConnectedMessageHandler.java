package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

public class NewParticipantConnectedMessageHandler implements GameMessagesHandler {

	@Override
	public synchronized void reactToMessages(GameMessageDto messageObj,
			RepositoriesAdapterForLiveGaming repositoriesAdapter) {
		
		WebSocketSessionsRepository webSocketSessionsRepository = repositoriesAdapter.getWebSocketSessionsRepository();
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
	}

}
