package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;

public interface GameMessagesHandler {

	void reactToMessages(GameMessageDto messageObj, RepositoriesAdapterForLiveGaming repositoriesAdapter);

}
