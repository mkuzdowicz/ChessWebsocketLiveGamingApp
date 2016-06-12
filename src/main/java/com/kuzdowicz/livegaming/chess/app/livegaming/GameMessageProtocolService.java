package com.kuzdowicz.livegaming.chess.app.livegaming;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.constants.ChessMoveStatus;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.ChessMoveDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersAccountsRepository;

@Service
public class GameMessageProtocolService {

	private final static Logger logger = LoggerFactory.getLogger(GameMessageProtocolService.class);

	private final WebSocketSessionsRepository webSocketSessionsRepository;
	private final LiveGamingUsersRepository liveGamingUsersRepository;
	private final LiveChessGamesRepository liveChessGamesRepository;
	private final Gson gson;
	private final ChessGamesRepository chessGamesRepository;
	private final UsersAccountsRepository usersRepository;

	@Autowired
	public GameMessageProtocolService(WebSocketSessionsRepository webSocketSessionsRepository,
			LiveGamingUsersRepository liveGamingUsersRepository, LiveChessGamesRepository liveChessGamesRepository,
			Gson gson, ChessGamesRepository chessGamesRepository, UsersAccountsRepository usersRepository) {
		this.webSocketSessionsRepository = webSocketSessionsRepository;
		this.liveGamingUsersRepository = liveGamingUsersRepository;
		this.liveChessGamesRepository = liveChessGamesRepository;
		this.gson = gson;
		this.chessGamesRepository = chessGamesRepository;
		this.usersRepository = usersRepository;
	}

	public synchronized void proccessMessage(GameMessageDto messageObj, String messageJsonString) {

		String messageType = messageObj.getType();
		if (messageType.equals(GameMessageType.GAME_HANDSHAKE_INVITATION)) {

			setUserComStatusIsDuringHandshakeSendMsgAndRefresh(messageObj, messageJsonString);

		} else if (messageType.equals(GameMessageType.GAME_HANDSHAKE_AGREEMENT)) {

			setUserComStatusIsPlayingAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.GAME_HANDSHAKE_REFUSE)) {

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.CHESS_MOVE)) {

			String fromUsername = messageObj.getSendFrom();
			LiveGamingUserDto fromUser = liveGamingUsersRepository.getWebsocketUser(fromUsername);

			if (isUserPlayingWithAnyUser(fromUser)) {

				String toUsername = messageObj.getSendTo();
				LiveGamingUserDto toUser = liveGamingUsersRepository.getWebsocketUser(toUsername);

				if (toUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

					if (userONEPlayWithUserTWO(fromUser, toUser)) {

						ChessMoveDto currentMove = messageObj.getChessMove();

						liveChessGamesRepository.addActualMoveToThisGameObject(toUser.getUniqueActualGameHash(),
								currentMove);
						liveChessGamesRepository.incrementNumberOfMoves(toUser.getUniqueActualGameHash());

						webSocketSessionsRepository.sendToSession(toUsername, fromUsername, messageJsonString);
					} else {
						logger.debug(messageObj.getSendFrom()
								+ " send message to user which he does not play with , ( to user: " + toUsername
								+ " )");
					}
				}
			} else {
				logger.debug(messageObj.getSendFrom() + " send chess-move but he his not playing with anyone");
			}

		} else if (messageType.equals(GameMessageType.GAME_OVER) || messageType.equals(GameMessageType.QUIT_GAME)
				|| messageType.equals(GameMessageType.USER_DISCONNECT)) {

			if (messageType.equals(GameMessageType.QUIT_GAME) || messageType.equals(GameMessageType.GAME_OVER)) {

				saveStatisticsDataToDbIfQuitGameOrIfCheckMate(messageObj);

			}

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.USER_CONNECT)) {
			webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
		}

	}

	private synchronized void saveStatisticsDataToDbIfQuitGameOrIfCheckMate(GameMessageDto messageObj) {

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

	private synchronized Boolean userONEPlayWithUserTWO(LiveGamingUserDto fromUser, LiveGamingUserDto toUser) {
		if (fromUser != null && toUser != null && fromUser.getPlayNowWithUser().equals(toUser.getUsername())
				&& toUser.getPlayNowWithUser().equals(fromUser.getUsername())) {
			return true;
		} else {
			return false;
		}
	}

	private synchronized Boolean isUserPlayingWithAnyUser(LiveGamingUserDto user) {
		if (user != null && user.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)
				&& user.getPlayNowWithUser() != null && !user.getPlayNowWithUser().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private synchronized void setUserComStatusIsDuringHandshakeSendMsgAndRefresh(GameMessageDto messageObj,
			String messageJsonString) {
		LiveGamingUserDto invitedUser = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());

		if (invitedUser != null
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_DURING_HANDSHAKE)
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

			liveGamingUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendFrom());
			liveGamingUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendTo());
			liveGamingUsersRepository.setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());

			LiveGamingUserDto sendToObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());
			messageObj.setSendToObj(sendToObj);

			LiveGamingUserDto sendFromObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendFrom());
			messageObj.setSendFromObj(sendFromObj);

			sendMessageToOneUser(messageObj, gson.toJson(messageObj));

			webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
		} else {
			logger.debug("invited user is already playing, is during handshake or is null");

			GameMessageDto tryLaterMsg = new GameMessageDto();
			tryLaterMsg.setType(GameMessageType.TRY_LATER);

			webSocketSessionsRepository.sendToSession(messageObj.getSendFrom(), "server", gson.toJson(tryLaterMsg));
		}

	}

	private synchronized void setUserComStatusIsPlayingAndRefresh(GameMessageDto messageObj) {

		String actualChessGameUUID = UUID.randomUUID().toString();

		liveGamingUsersRepository.setComStatusIsPlaying(messageObj.getSendTo(), messageObj.getSendFrom());
		liveGamingUsersRepository.setComStatusIsPlaying(messageObj.getSendFrom(), messageObj.getSendTo());

		LiveGamingUserDto sendToObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendTo());

		sendToObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendToObj(sendToObj);

		LiveGamingUserDto sendFromObj = liveGamingUsersRepository.getWebsocketUser(messageObj.getSendFrom());

		sendFromObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendFromObj(sendFromObj);
		messageObj.setMoveStatus(ChessMoveStatus.WHITE_TO_MOVE);

		ChessGame chessGame = prepareAndReturnChessGameObjectAtGameStart(actualChessGameUUID, sendToObj, sendFromObj,
				messageObj);

		liveChessGamesRepository.addNewGame(chessGame);

		sendMessageToOneUser(messageObj, gson.toJson(messageObj));

		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

	private synchronized ChessGame prepareAndReturnChessGameObjectAtGameStart(String actualChessGameUUID,
			LiveGamingUserDto sendToObj, LiveGamingUserDto sendFromObj, GameMessageDto messageObj) {

		ChessGame chessGame = new ChessGame();
		chessGame.setUniqueGameHash(actualChessGameUUID);
		chessGame.setBeginDate(new Date());
		chessGame.setNumberOfMoves(0);

		if (sendToObj.getChessColor().equals("white")) {
			chessGame.setWhitePlayerName(sendToObj.getUsername());
			chessGame.setBlackPlayerName(sendFromObj.getUsername());
		} else {
			chessGame.setWhitePlayerName(sendFromObj.getUsername());
			chessGame.setBlackPlayerName(sendToObj.getUsername());
		}

		chessGame.setEndingGameFENString(messageObj.getFen());

		return chessGame;
	}

	private synchronized void setUserComStatusWaitForNewGameAndRefresh(GameMessageDto messageObj) {

		liveGamingUsersRepository.setComStatusWaitForNewGame(messageObj.getSendFrom());
		liveGamingUsersRepository.setComStatusWaitForNewGame(messageObj.getSendTo());
		liveGamingUsersRepository.setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());
		webSocketSessionsRepository.sendToAllConnectedSessionsActualParticipantList();
	}

	private synchronized void sendMessageToOneUser(GameMessageDto message, String content) {

		String toUsername = message.getSendTo();
		String fromUsername = message.getSendFrom();
		if (toUsername != null && StringUtils.isNotEmpty(toUsername)) {

			webSocketSessionsRepository.sendToSession(toUsername, fromUsername, content);
		}
	}

}
