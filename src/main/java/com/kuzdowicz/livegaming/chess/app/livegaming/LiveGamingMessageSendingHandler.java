package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies.GameMessageHandlerFacotry;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies.GameMessagesHandler;

@Service
public class LiveGamingMessageSendingHandler {

	private final LiveGamingContextAdapter liveGamingContextAdapter;

	@Autowired
	public LiveGamingMessageSendingHandler(LiveGamingContextAdapter liveGamingContextAdapter) {
		this.liveGamingContextAdapter = liveGamingContextAdapter;
	}

	public synchronized void proccessMessage(GameMessageDto messageDto) {

		GameMessageHandlerFacotry gameMessageHandlerFacotry = new GameMessageHandlerFacotry();
		GameMessagesHandler messageHandler = gameMessageHandlerFacotry.createMessageHandler(messageDto.getType());

		messageHandler.reactToMessages(messageDto, liveGamingContextAdapter);

	}

}
