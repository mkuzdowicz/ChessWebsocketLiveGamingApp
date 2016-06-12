package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategys;

import java.util.Date;

import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.db.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveGamingUsersRepository;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.RepositoriesAdapterForLiveGaming;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.WebSocketSessionsRepository;

public class GameEndMessageHandler implements GameMessagesHandler {

	@Override
	public synchronized void reactToMessages(GameMessageDto messageObj,
			RepositoriesAdapterForLiveGaming repositoriesAdapter) {

		String messageType = messageObj.getType();
		LiveGamingUsersRepository liveGamingUsersRepository = repositoriesAdapter.getLiveGamingUsersRepository();
		WebSocketSessionsRepository webSocketSessionsRepository = repositoriesAdapter.getWebSocketSessionsRepository();

		if (messageType.equals(GameMessageType.QUIT_GAME) || messageType.equals(GameMessageType.GAME_OVER)) {
			saveStatisticsDataToDbIfQuitGameOrIfCheckMate(messageObj, repositoriesAdapter);
		}

		liveGamingUsersRepository.resetPlayersPairStateToWiatForNewGame(messageObj);
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

	private void saveStatisticsDataToDbIfQuitGameOrIfCheckMate(GameMessageDto messageObj,
			RepositoriesAdapterForLiveGaming repositoriesAdapter) {

		LiveGamingUsersRepository liveGamingUsersRepository = repositoriesAdapter.getLiveGamingUsersRepository();
		LiveChessGamesRepository liveChessGamesRepository = repositoriesAdapter.getLiveChessGamesRepository();
		UsersAccountsRepository usersRepository = repositoriesAdapter.getUsersRepository();
		ChessGamesRepository chessGamesRepository = repositoriesAdapter.getChessGamesRepository();

		LiveGamingUserDto webSocketUserObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendFrom());
		ChessGame game = liveChessGamesRepository.getGameByUniqueHashId(webSocketUserObj.getUniqueActualGameHash());
		game.setEndDate(new Date());
		game.setEndingGameFENString(messageObj.getFen());
		liveChessGamesRepository.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

		if (messageObj.getCheckMate() != null && messageObj.getCheckMate() == true) {
			game.setCheckMate(true);
		} else {
			game.setCheckMate(false);
		}

		UserAccount user1 = usersRepository.findOneByUsername(messageObj.getSendFrom());

		Long user1NumberOfGamesPlayed = user1.getNumberOfGamesPlayed();

		if (user1NumberOfGamesPlayed == null) {
			user1.setNumberOfGamesPlayed(new Long(1));
		} else {
			user1NumberOfGamesPlayed++;
			user1.setNumberOfGamesPlayed(user1NumberOfGamesPlayed);
		}

		usersRepository.save(user1);

		UserAccount user2 = usersRepository.findOneByUsername(messageObj.getSendTo());

		Long user2NumberOfGamesPlayed = user2.getNumberOfGamesPlayed();

		if (user2NumberOfGamesPlayed == null) {
			user2.setNumberOfGamesPlayed(new Long(1));
		} else {
			user2NumberOfGamesPlayed++;
			user2.setNumberOfGamesPlayed(user2NumberOfGamesPlayed);
		}

		usersRepository.save(user2);

		if (game.getCheckMate() == true) {

			game.setWinnerName(messageObj.getSendFrom());
			game.setLoserName(messageObj.getSendTo());

			UserAccount winner = usersRepository.findOneByUsername(game.getWinnerName());

			Long winnerNumberOfWonGames = winner.getNumberOfWonChessGames();

			if (winnerNumberOfWonGames == null) {
				winner.setNumberOfWonChessGames(new Long(1));
			} else {
				winnerNumberOfWonGames++;
				winner.setNumberOfWonChessGames(winnerNumberOfWonGames);
			}
			usersRepository.save(winner);

			UserAccount looser = usersRepository.findOneByUsername(game.getLoserName());

			Long looserNumberOfLostGames = looser.getNumberOfLostChessGames();

			if (looserNumberOfLostGames == null) {
				looser.setNumberOfLostChessGames(new Long(1));
			} else {
				looserNumberOfLostGames++;
				looser.setNumberOfWonChessGames(looserNumberOfLostGames);
			}
			usersRepository.save(looser);
		}
		chessGamesRepository.save(game);
	}

}
