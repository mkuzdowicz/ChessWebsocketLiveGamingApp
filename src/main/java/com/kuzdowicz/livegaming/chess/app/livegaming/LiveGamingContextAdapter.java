package com.kuzdowicz.livegaming.chess.app.livegaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

@Component
public class LiveGamingContextAdapter {

	private final WebSocketSessionsRegistry webSocketSessionsRegistry;
	private final LiveGamingUsersRegistry liveGamingUsersRegistry;
	private final LiveChessGamesRegistry liveChessGamesRegistry;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	@Autowired
	public LiveGamingContextAdapter(WebSocketSessionsRegistry webSocketSessionsRegistry,
			LiveGamingUsersRegistry liveGamingUsersRegistry, LiveChessGamesRegistry liveChessGamesRegistry,
			ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRegistry = webSocketSessionsRegistry;
		this.liveGamingUsersRegistry = liveGamingUsersRegistry;
		this.liveChessGamesRegistry = liveChessGamesRegistry;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public WebSocketSessionsRegistry getWebSocketSessionsRegistry() {
		return webSocketSessionsRegistry;
	}

	public LiveGamingUsersRegistry getLiveGamingUsersRegistry() {
		return liveGamingUsersRegistry;
	}

	public LiveChessGamesRegistry getLiveChessGamesRegistry() {
		return liveChessGamesRegistry;
	}

	public ChessGamesRepository getChessGamesRepository() {
		return chessGamesRepository;
	}

	public UsersAccountsRepository getUsersRepository() {
		return usersRepository;
	}

}
