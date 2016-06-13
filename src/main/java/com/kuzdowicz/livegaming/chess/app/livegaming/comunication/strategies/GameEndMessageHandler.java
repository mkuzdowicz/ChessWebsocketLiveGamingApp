package com.kuzdowicz.livegaming.chess.app.livegaming.comunication.strategies;

import java.util.Date;

import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.db.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.db.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.livegaming.LiveGamingContextAdapter;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveChessGamesRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.LiveGamingUsersRegistry;
import com.kuzdowicz.livegaming.chess.app.livegaming.registries.WebSocketSessionsRegistry;

public class GameEndMessageHandler implements GameMessagesHandler {

	@Override
	public synchronized void reactToMessages(GameMessageDto messageDto,
			LiveGamingContextAdapter gamingCtxAdapter) {

		String messageType = messageDto.getType();
		LiveGamingUsersRegistry liveGamingUsersRegistry = gamingCtxAdapter.getLiveGamingUsersRegistry();
		WebSocketSessionsRegistry webSocketSessionsRegistry = gamingCtxAdapter.getWebSocketSessionsRegistry();

		if (messageType.equals(GameMessageType.QUIT_GAME) || messageType.equals(GameMessageType.GAME_OVER)) {
			saveStatisticsDataToDbIfQuitGameOrIfCheckMate(messageDto, gamingCtxAdapter);
		}

		liveGamingUsersRegistry.resetPlayersPairStateToWiatForNewGame(messageDto);
		webSocketSessionsRegistry.sendToAllConnectedSessionsActualParticipantList();

	}

	private void saveStatisticsDataToDbIfQuitGameOrIfCheckMate(GameMessageDto messageDto,
			LiveGamingContextAdapter gamingCtxAdapter) {

		LiveGamingUsersRegistry liveGamingUsersRegistry = gamingCtxAdapter.getLiveGamingUsersRegistry();
		LiveChessGamesRegistry liveChessGamesRegistry = gamingCtxAdapter.getLiveChessGamesRegistry();
		
		UsersAccountsRepository usersRepository = gamingCtxAdapter.getUsersRepository();
		ChessGamesRepository chessGamesRepository = gamingCtxAdapter.getChessGamesRepository();

		LiveGamingUserDto webSocketUserObj = liveGamingUsersRegistry.getWebsocketUser(messageDto.getSendFrom());
		ChessGame game = liveChessGamesRegistry.getGameByUniqueHashId(webSocketUserObj.getUniqueActualGameHash());
		game.setEndDate(new Date());
		game.setEndingGameFENString(messageDto.getFen());
		liveChessGamesRegistry.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

		if (messageDto.getCheckMate() != null && messageDto.getCheckMate() == true) {
			game.setCheckMate(true);
		} else {
			game.setCheckMate(false);
		}

		UserAccount user1 = usersRepository.findOneByUsername(messageDto.getSendFrom());

		Long user1NumberOfGamesPlayed = user1.getNumberOfGamesPlayed();

		if (user1NumberOfGamesPlayed == null) {
			user1.setNumberOfGamesPlayed(new Long(1));
		} else {
			user1NumberOfGamesPlayed++;
			user1.setNumberOfGamesPlayed(user1NumberOfGamesPlayed);
		}

		usersRepository.save(user1);

		UserAccount user2 = usersRepository.findOneByUsername(messageDto.getSendTo());

		Long user2NumberOfGamesPlayed = user2.getNumberOfGamesPlayed();

		if (user2NumberOfGamesPlayed == null) {
			user2.setNumberOfGamesPlayed(new Long(1));
		} else {
			user2NumberOfGamesPlayed++;
			user2.setNumberOfGamesPlayed(user2NumberOfGamesPlayed);
		}

		usersRepository.save(user2);

		if (game.getCheckMate() == true) {

			game.setWinnerName(messageDto.getSendFrom());
			game.setLoserName(messageDto.getSendTo());

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
