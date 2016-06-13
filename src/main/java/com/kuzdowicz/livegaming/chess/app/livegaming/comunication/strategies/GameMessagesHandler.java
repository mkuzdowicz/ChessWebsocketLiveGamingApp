package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;

public interface GameMessagesHandler {

	void reactToMessages(GameMessageDto messageDto, LiveGamingContextAdapter gamingCtxAdapter);

}
