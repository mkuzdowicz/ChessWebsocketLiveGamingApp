package com.kuzdowicz.livegaming.chess.app.livegaming.repositories;

import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;

public class RepositoriesAdapterForLiveGaming {

	private final WebSocketSessionsRepository webSocketSessionsRepository;
	private final LiveGamingUsersRepository liveGamingUsersRepository;
	private final LiveChessGamesRepository liveChessGamesRepository;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	private RepositoriesAdapterForLiveGaming(WebSocketSessionsRepository webSocketSessionsRepository,
			LiveGamingUsersRepository liveGamingUsersRepository, LiveChessGamesRepository liveChessGamesRepository,
			ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRepository = webSocketSessionsRepository;
		this.liveGamingUsersRepository = liveGamingUsersRepository;
		this.liveChessGamesRepository = liveChessGamesRepository;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public static class Builder {

		private WebSocketSessionsRepository webSocketSessionsRepository;
		private LiveGamingUsersRepository liveGamingUsersRepository;
		private LiveChessGamesRepository liveChessGamesRepository;
		private ChessGamesRepository chessGamesRepository;
		private UsersAccountsRepository usersRepository;

		public Builder webSocketSessionsRepository(WebSocketSessionsRepository webSocketSessionsRepository) {
			this.webSocketSessionsRepository = webSocketSessionsRepository;
			return this;
		}

		public Builder liveGamingUsersRepository(LiveGamingUsersRepository liveGamingUsersRepository) {
			this.liveGamingUsersRepository = liveGamingUsersRepository;
			return this;
		}

		public Builder liveChessGamesRepository(LiveChessGamesRepository liveChessGamesRepository) {
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

		public RepositoriesAdapterForLiveGaming build() {
			return new RepositoriesAdapterForLiveGaming(webSocketSessionsRepository, liveGamingUsersRepository,
					liveChessGamesRepository, chessGamesRepository, usersRepository);
		}

	}

	public WebSocketSessionsRepository getWebSocketSessionsRepository() {
		return webSocketSessionsRepository;
	}

	public LiveGamingUsersRepository getLiveGamingUsersRepository() {
		return liveGamingUsersRepository;
	}

	public LiveChessGamesRepository getLiveChessGamesRepository() {
		return liveChessGamesRepository;
	}

	public ChessGamesRepository getChessGamesRepository() {
		return chessGamesRepository;
	}

	public UsersAccountsRepository getUsersRepository() {
		return usersRepository;
	}

}
