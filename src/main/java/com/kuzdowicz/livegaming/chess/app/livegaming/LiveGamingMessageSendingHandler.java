package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies.GameMessageHandlerFacotry;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies.GameMessagesHandler;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

@Service
public class LiveGamingMessageSendingHandler {

	private final WebSocketSessionsRegistry webSocketSessionsRegistry;
	private final LiveGamingUsersRegistry liveGamingUsersRegistry;
	private final LiveChessGamesRegistry liveChessGamesRegistry;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	@Autowired
	public LiveGamingMessageSendingHandler(WebSocketSessionsRegistry webSocketSessionsRegistry,
			LiveGamingUsersRegistry liveGamingUsersRegistry, LiveChessGamesRegistry liveChessGamesRegistry,
			ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRegistry = webSocketSessionsRegistry;
		this.liveGamingUsersRegistry = liveGamingUsersRegistry;
		this.liveChessGamesRegistry = liveChessGamesRegistry;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public synchronized void proccessMessage(GameMessageDto messageObj) {

		GameMessageHandlerFacotry gameMessageHandlerFacotry = new GameMessageHandlerFacotry();
		GameMessagesHandler messageHandler = gameMessageHandlerFacotry.createMessageHandler(messageObj.getType());

		LiveGamingContextAdapter repositoriesAdapter = //
				new LiveGamingContextAdapter.Builder() //
						.webSocketSessionsRepository(webSocketSessionsRegistry) //
						.liveGamingUsersRepository(liveGamingUsersRegistry) //
						.liveChessGamesRepository(liveChessGamesRegistry) //
						.chessGamesRepository(chessGamesRepository) //
						.usersRepository(usersRepository).build();

		messageHandler.reactToMessages(messageObj, repositoriesAdapter);

	}

}
