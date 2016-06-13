package com.kuzdowicz.livegaming.chess.app.livegaming;

import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class LiveGamingContextAdapter {

	private final WebSocketSessionsRegistry webSocketSessionsRepository;
	private final LiveGamingUsersRegistry liveGamingUsersRepository;
	private final LiveChessGamesRegistry liveChessGamesRepository;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	private LiveGamingContextAdapter(WebSocketSessionsRegistry webSocketSessionsRepository,
			LiveGamingUsersRegistry liveGamingUsersRepository, LiveChessGamesRegistry liveChessGamesRepository,
			ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRepository = webSocketSessionsRepository;
		this.liveGamingUsersRepository = liveGamingUsersRepository;
		this.liveChessGamesRepository = liveChessGamesRepository;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public static class Builder {

		private WebSocketSessionsRegistry webSocketSessionsRepository;
		private LiveGamingUsersRegistry liveGamingUsersRepository;
		private LiveChessGamesRegistry liveChessGamesRepository;
		private ChessGamesRepository chessGamesRepository;
		private UsersAccountsRepository usersRepository;

		public Builder webSocketSessionsRepository(WebSocketSessionsRegistry webSocketSessionsRepository) {
			this.webSocketSessionsRepository = webSocketSessionsRepository;
			return this;
		}

		public Builder liveGamingUsersRepository(LiveGamingUsersRegistry liveGamingUsersRepository) {
			this.liveGamingUsersRepository = liveGamingUsersRepository;
			return this;
		}

		public Builder liveChessGamesRepository(LiveChessGamesRegistry liveChessGamesRepository) {
			this.liveChessGamesRepository = liveChessGamesRepository;
			return this;
		}

		public Builder chessGamesRepository(ChessGamesRepository chessGamesRepository) {
			this.chessGamesRepository = chessGamesRepository;
			return this;
		}

		public Builder usersRepository(UsersAccountsRepository usersRepository) {
			this.usersRepository = usersRepository;
			return this;
		}

		public LiveGamingContextAdapter build() {
			return new LiveGamingContextAdapter(webSocketSessionsRepository, liveGamingUsersRepository,
					liveChessGamesRepository, chessGamesRepository, usersRepository);
		}

	}

	public WebSocketSessionsRegistry getWebSocketSessionsRepository() {
		return webSocketSessionsRepository;
	}

	public LiveGamingUsersRegistry getLiveGamingUsersRepository() {
		return liveGamingUsersRepository;
	}

	public LiveChessGamesRegistry getLiveChessGamesRepository() {
		return liveChessGamesRepository;
	}

	public ChessGamesRepository getChessGamesRepository() {
		return chessGamesRepository;
	}

	public UsersAccountsRepository getUsersRepository() {
		return usersRepository;
	}

}
