package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys.GameMessageHandlerFacotry;
import com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys.GameMessagesHandler;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveGamingUsersRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

@Service
public class LiveGamingMessageSendingHandler {

	private final WebSocketSessionsRepository webSocketSessionsRepository;
	private final LiveGamingUsersRepository liveGamingUsersRepository;
	private final LiveChessGamesRepository liveChessGamesRepository;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	@Autowired
	public LiveGamingMessageSendingHandler(WebSocketSessionsRepository webSocketSessionsRepository,
			LiveGamingUsersRepository liveGamingUsersRepository, LiveChessGamesRepository liveChessGamesRepository,
			ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRepository = webSocketSessionsRepository;
		this.liveGamingUsersRepository = liveGamingUsersRepository;
		this.liveChessGamesRepository = liveChessGamesRepository;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public synchronized void proccessMessage(GameMessageDto messageObj) {

		GameMessageHandlerFacotry gameMessageHandlerFacotry = new GameMessageHandlerFacotry();
		GameMessagesHandler messageHandler = gameMessageHandlerFacotry.createMessageHandler(messageObj.getType());

		RepositoriesAdapterForLiveGaming repositoriesAdapter = //
				new RepositoriesAdapterForLiveGaming.Builder() //
						.webSocketSessionsRepository(webSocketSessionsRepository) //
						.liveGamingUsersRepository(liveGamingUsersRepository) //
						.liveChessGamesRepository(liveChessGamesRepository) //
						.chessGamesRepository(chessGamesRepository) //
						.usersRepository(usersRepository).build();

		messageHandler.reactToMessages(messageObj, repositoriesAdapter);

	}

}
