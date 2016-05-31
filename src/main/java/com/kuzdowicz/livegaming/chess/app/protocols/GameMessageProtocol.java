package com.kuzdowicz.livegaming.chess.app.protocols;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.models.ChessGame;
import com.kuzdowicz.livegaming.chess.app.models.ChessMove;
import com.kuzdowicz.livegaming.chess.app.models.GameMessage;
import com.kuzdowicz.livegaming.chess.app.models.GameUser;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;
import com.kuzdowicz.livegaming.chess.app.websockets.ChessGamesHandler;
import com.kuzdowicz.livegaming.chess.app.websockets.GameUsersHandler;
import com.kuzdowicz.livegaming.chess.app.websockets.WebSocketSessionHandler;

@Service
public class GameMessageProtocol {

	private final static Logger log = Logger
			.getLogger(GameMessageProtocol.class);

	private WebSocketSessionHandler sessionHandler;

	private GameUsersHandler usersHandler;

	private ChessGamesHandler chessGamesHandler;

	private Gson gson;

	@Autowired
	private ChessGamesRepository chessGamesRepository;

	@Autowired
	private UsersRepository usersRepository;

	public GameMessageProtocol(WebSocketSessionHandler sessionHandler,
			GameUsersHandler usesrHandler,
			ChessGamesHandler chessGamesHandler) {
		this.sessionHandler = sessionHandler;
		this.usersHandler = usesrHandler;
		this.chessGamesHandler = chessGamesHandler;
		gson = new Gson();

		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	public GameMessageProtocol() {

	}

	public synchronized void proccessMessage(GameMessage messageObj,
			String messageJsonString) {

		String messageType = messageObj.getType();

		if (messageType.equals(GameMessageType.GAME_HANDSHAKE_INVITATION)) {

			setUserComStatusIsDuringHandshakeSendMsgAndRefresh(messageObj,
					messageJsonString);

		} else if (messageType
				.equals(GameMessageType.GAME_HANDSHAKE_AGREEMENT)) {

			setUserComStatusIsPlayingAndRefresh(messageObj);

		} else if (messageType
				.equals(GameMessageType.GAME_HANDSHAKE_REFUSE)) {

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.CHESS_MOVE)) {

			String fromUsername = messageObj.getSendFrom();
			GameUser fromUser = usersHandler
					.getWebsocketUser(fromUsername);

			if (isUserPlayingWithAnyUser(fromUser)) {

				String toUsername = messageObj.getSendTo();
				GameUser toUser = usersHandler
						.getWebsocketUser(toUsername);

				if (toUser.getCommunicationStatus().equals(
						GameUserCommunicationStatus.IS_PLAYING)) {

					if (userONEPlayWithUserTWO(fromUser, toUser)) {

						ChessMove currentMove = messageObj.getChessMove();

						chessGamesHandler.addActualMoveToThisGameObject(
								toUser.getUniqueActualGameHash(), currentMove);

						chessGamesHandler.incrementNumberOfMoves(toUser
								.getUniqueActualGameHash());

						sessionHandler.sendToSession(toUsername, fromUsername,
								messageJsonString);
					} else {
						log.debug(messageObj.getSendFrom()
								+ " send message to user which he does not play with , ( to user: "
								+ toUsername + " )");
					}
				}
			} else {
				log.debug(messageObj.getSendFrom()
						+ " send chess-move but he his not playing with anyone");
			}

		} else if (messageType.equals(GameMessageType.GAME_OVER)
				|| messageType.equals(GameMessageType.QUIT_GAME)
				|| messageType.equals(GameMessageType.USER_DISCONNECT)) {

			if (messageType.equals(GameMessageType.QUIT_GAME)
					|| messageType.equals(GameMessageType.GAME_OVER)) {

				saveStatisticsDataToDbIfQuitGameOrIfCheckMate(messageObj);

			}

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.USER_CONNECT)) {

			log.debug("user " + messageObj.getSendFrom()
					+ " join to participants");

			sessionHandler.sendToAllConnectedSessionsActualParticipantList();
		}

	}

	private synchronized void saveStatisticsDataToDbIfQuitGameOrIfCheckMate(
			GameMessage messageObj) {

		GameUser webSocketUserObj = usersHandler
				.getWebsocketUser(messageObj.getSendFrom());
		ChessGame game = chessGamesHandler
				.getGameByUniqueHashId(webSocketUserObj
						.getUniqueActualGameHash());
		game.setEndDate(new Date());
		game.setEndingGameFENString(messageObj.getFen());
		ChessGamesHandler
				.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

		if (messageObj.getCheckMate() != null
				&& messageObj.getCheckMate() == true) {
			game.setCheckMate(true);
		} else {
			game.setCheckMate(false);
		}

		UserAccount user1 = usersRepository.findOneByUsername(messageObj
				.getSendFrom());

		Long user1NumberOfGamesPlayed = user1.getNumberOfGamesPlayed();

		if (user1NumberOfGamesPlayed == null) {
			user1.setNumberOfGamesPlayed(new Long(1));
		} else {
			user1NumberOfGamesPlayed++;
			user1.setNumberOfGamesPlayed(user1NumberOfGamesPlayed);
		}

		// save to DB
		usersRepository.save(user1);

		UserAccount user2 = usersRepository.findOneByUsername(messageObj
				.getSendTo());

		Long user2NumberOfGamesPlayed = user2.getNumberOfGamesPlayed();

		if (user2NumberOfGamesPlayed == null) {
			user2.setNumberOfGamesPlayed(new Long(1));
		} else {
			user2NumberOfGamesPlayed++;
			user2.setNumberOfGamesPlayed(user2NumberOfGamesPlayed);
		}

		// save to DB
		usersRepository.save(user2);

		if (game.getCheckMate() == true) {

			game.setWinnerUsername(messageObj.getSendFrom());
			game.setLoserUsername(messageObj.getSendTo());

			// winner -----------------------------------
			UserAccount winner = usersRepository.findOneByUsername(game
					.getWinnerUsername());

			Long winnerNumberOfWonGames = winner.getNumberOfWonChessGames();

			if (winnerNumberOfWonGames == null) {
				winner.setNumberOfWonChessGames(new Long(1));
			} else {
				winnerNumberOfWonGames++;
				winner.setNumberOfWonChessGames(winnerNumberOfWonGames);
			}

			usersRepository.save(winner);

			// looser ----------------------------------

			UserAccount looser = usersRepository.findOneByUsername(game
					.getLoserUsername());

			Long looserNumberOfLostGames = looser.getNumberOfLostChessGames();

			if (looserNumberOfLostGames == null) {
				looser.setNumberOfLostChessGames(new Long(1));
			} else {
				looserNumberOfLostGames++;
				looser.setNumberOfWonChessGames(looserNumberOfLostGames);
			}

			usersRepository.save(looser);
			// sessionHandler.sendToSession(game.getLoserUsername(),
			// game.getWinnerUsername(), gson.toJson(messageObj));

		}

		chessGamesRepository.save(game);
	}

	private synchronized Boolean userONEPlayWithUserTWO(
			GameUser fromUser, GameUser toUser) {
		log.debug("userONEPlayWithUserTWO()");

		if (fromUser != null && toUser != null
				&& fromUser.getPlayNowWithUser().equals(toUser.getUsername())
				&& toUser.getPlayNowWithUser().equals(fromUser.getUsername())) {
			return true;
		} else {
			return false;
		}

	}

	private synchronized Boolean isUserPlayingWithAnyUser(GameUser user) {
		log.debug("isUserPlayingWithAnyUser()");

		if (user != null
				&& user.getCommunicationStatus().equals(
						GameUserCommunicationStatus.IS_PLAYING)
				&& user.getPlayNowWithUser() != null
				&& !user.getPlayNowWithUser().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private synchronized void setUserComStatusIsDuringHandshakeSendMsgAndRefresh(
			GameMessage messageObj, String messageJsonString) {
		log.debug("setUserComStatusIsDuringHandshakeAndRefresh()");

		GameUser invitedUser = usersHandler
				.getWebsocketUser(messageObj.getSendTo());

		if (invitedUser != null
				&& !invitedUser.getCommunicationStatus().equals(
						GameUserCommunicationStatus.IS_DURING_HANDSHAKE)
				&& !invitedUser.getCommunicationStatus().equals(
						GameUserCommunicationStatus.IS_PLAYING)) {

			usersHandler
					.setComStatusIsDuringHandshake(messageObj.getSendFrom());
			usersHandler.setComStatusIsDuringHandshake(messageObj.getSendTo());

			usersHandler.setChessPiecesColorForGamers(messageObj.getSendTo(),
					messageObj.getSendFrom());

			GameUser sendToObj = usersHandler
					.getWebsocketUser(messageObj.getSendTo());

			messageObj.setSendToObj(sendToObj);

			GameUser sendFromObj = usersHandler
					.getWebsocketUser(messageObj.getSendFrom());

			messageObj.setSendFromObj(sendFromObj);

			sendMessageToOneUser(messageObj, gson.toJson(messageObj));

			sessionHandler.sendToAllConnectedSessionsActualParticipantList();
		} else {
			log.debug("invited user is already playing, is during handshake or is null");

			GameMessage tryLaterMsg = new GameMessage();
			tryLaterMsg.setType(GameMessageType.TRY_LATER);

			sessionHandler.sendToSession(messageObj.getSendFrom(), "server",
					gson.toJson(tryLaterMsg));
		}

	}

	private synchronized void setUserComStatusIsPlayingAndRefresh(
			GameMessage messageObj) {
		log.debug("setUserComStatusIsPlayingAndRefresh()");

		String actualChessGameUUID = UUID.randomUUID().toString();

		usersHandler.setComStatusIsPlaying(messageObj.getSendTo(),
				messageObj.getSendFrom());
		usersHandler.setComStatusIsPlaying(messageObj.getSendFrom(),
				messageObj.getSendTo());

		GameUser sendToObj = usersHandler.getWebsocketUser(messageObj
				.getSendTo());

		sendToObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendToObj(sendToObj);

		GameUser sendFromObj = usersHandler
				.getWebsocketUser(messageObj.getSendFrom());

		sendFromObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendFromObj(sendFromObj);
		messageObj.setMoveStatus(ChessMoveStatus.WHITE_TO_MOVE);

		ChessGame chessGame = prepareAndReturnChessGameObjectAtGameStart(
				actualChessGameUUID, sendToObj, sendFromObj, messageObj);

		chessGamesHandler.addNewGame(chessGame);

		sendMessageToOneUser(messageObj, gson.toJson(messageObj));

		sessionHandler.sendToAllConnectedSessionsActualParticipantList();

	}

	private synchronized ChessGame prepareAndReturnChessGameObjectAtGameStart(
			String actualChessGameUUID, GameUser sendToObj,
			GameUser sendFromObj, GameMessage messageObj) {
		log.debug("prepareAndReturnChessGameObjectAtGameStart()");

		ChessGame chessGame = new ChessGame();
		chessGame.setUniqueGameHash(actualChessGameUUID);
		chessGame.setBeginDate(new Date());
		chessGame.setNumberOfMoves(0);

		if (sendToObj.getChessColor().equals("white")) {
			chessGame.setWhiteColUsername(sendToObj.getUsername());
			chessGame.setBlackColUsername(sendFromObj.getUsername());
		} else {
			chessGame.setWhiteColUsername(sendFromObj.getUsername());
			chessGame.setBlackColUsername(sendToObj.getUsername());
		}

		chessGame.setEndingGameFENString(messageObj.getFen());

		return chessGame;
	}

	private synchronized void setUserComStatusWaitForNewGameAndRefresh(
			GameMessage messageObj) {
		log.debug("setUserComStatusWaitForNewGameAndRefresh()");

		usersHandler.setComStatusWaitForNewGame(messageObj.getSendFrom());
		usersHandler.setComStatusWaitForNewGame(messageObj.getSendTo());
		usersHandler.setChessPiecesColorForGamers(messageObj.getSendTo(),
				messageObj.getSendFrom());

		sessionHandler.sendToAllConnectedSessionsActualParticipantList();
	}

	private synchronized void sendMessageToOneUser(GameMessage message,
			String content) {
		log.debug("sendMessageToOneUser()");
		log.debug("typ wiadomosci : " + message.getType());
		log.debug("od usera " + message.getSendFrom() + " do usera "
				+ message.getSendTo());

		String toUsername = message.getSendTo();
		String fromUsername = message.getSendFrom();
		if (toUsername != null && StringUtils.isNotEmpty(toUsername)) {

			sessionHandler.sendToSession(toUsername, fromUsername, content);
		}
	}

}
