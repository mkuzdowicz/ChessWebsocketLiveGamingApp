package com.kuzdowicz.livegaming.chess.app.protocols;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.constants.ChessMoveStatus;
import com.kuzdowicz.livegaming.chess.app.constants.GameMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.models.ChessGame;
import com.kuzdowicz.livegaming.chess.app.models.ChessMove;
import com.kuzdowicz.livegaming.chess.app.models.GameMessage;
import com.kuzdowicz.livegaming.chess.app.models.GameUser;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;
import com.kuzdowicz.livegaming.chess.app.websockets.ChessGamesHandler;
import com.kuzdowicz.livegaming.chess.app.websockets.GameUsersRepository;
import com.kuzdowicz.livegaming.chess.app.websockets.WebSocketSessionsRepository;

public class GameMessageProtocol {

	private final static Logger log = Logger.getLogger(GameMessageProtocol.class);

	private WebSocketSessionsRepository wsSesionsRepository;

	private GameUsersRepository gameUsersRepository;

	private ChessGamesHandler chessGamesHandler;

	private Gson gson;

	@Autowired
	private ChessGamesRepository chessGamesRepository;

	@Autowired
	private UsersRepository usersRepository;

	public GameMessageProtocol(WebSocketSessionsRepository wsSesionsRepository, GameUsersRepository usesrHandler,
			ChessGamesHandler chessGamesHandler) {
		this.wsSesionsRepository = wsSesionsRepository;
		this.gameUsersRepository = usesrHandler;
		this.chessGamesHandler = chessGamesHandler;
		gson = new Gson();

		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	public GameMessageProtocol() {

	}

	public synchronized void proccessMessage(GameMessage messageObj, String messageJsonString) {

		log.info("proccessMessage()");
		log.info(messageJsonString);

		String messageType = messageObj.getType();

		System.out.println(gson.toJson(messageObj));

		if (messageType.equals(GameMessageType.GAME_HANDSHAKE_INVITATION)) {

			setUserComStatusIsDuringHandshakeSendMsgAndRefresh(messageObj, messageJsonString);

		} else if (messageType.equals(GameMessageType.GAME_HANDSHAKE_AGREEMENT)) {

			setUserComStatusIsPlayingAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.GAME_HANDSHAKE_REFUSE)) {

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.CHESS_MOVE)) {

			String fromUsername = messageObj.getSendFrom();
			GameUser fromUser = gameUsersRepository.getWebsocketUser(fromUsername);

			if (isUserPlayingWithAnyUser(fromUser)) {

				String toUsername = messageObj.getSendTo();
				GameUser toUser = gameUsersRepository.getWebsocketUser(toUsername);

				if (toUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

					if (userONEPlayWithUserTWO(fromUser, toUser)) {

						ChessMove currentMove = messageObj.getChessMove();

						chessGamesHandler.addActualMoveToThisGameObject(toUser.getUniqueActualGameHash(), currentMove);

						chessGamesHandler.incrementNumberOfMoves(toUser.getUniqueActualGameHash());

						wsSesionsRepository.sendToSession(toUsername, fromUsername, messageJsonString);
					} else {
						log.debug(messageObj.getSendFrom()
								+ " send message to user which he does not play with , ( to user: " + toUsername
								+ " )");
					}
				}
			} else {
				log.debug(messageObj.getSendFrom() + " send chess-move but he his not playing with anyone");
			}

		} else if (messageType.equals(GameMessageType.GAME_OVER) || messageType.equals(GameMessageType.QUIT_GAME)
				|| messageType.equals(GameMessageType.USER_DISCONNECT)) {

			if (messageType.equals(GameMessageType.QUIT_GAME) || messageType.equals(GameMessageType.GAME_OVER)) {

				saveStatisticsDataToDbIfQuitGameOrIfCheckMate(messageObj);

			}

			sendMessageToOneUser(messageObj, messageJsonString);
			setUserComStatusWaitForNewGameAndRefresh(messageObj);

		} else if (messageType.equals(GameMessageType.USER_CONNECT)) {

			log.info("user " + messageObj.getSendFrom() + " join to participants");

			wsSesionsRepository.sendToAllConnectedSessionsActualParticipantList();
		}

	}

	private synchronized void saveStatisticsDataToDbIfQuitGameOrIfCheckMate(GameMessage messageObj) {

		GameUser webSocketUserObj = gameUsersRepository.getWebsocketUser(messageObj.getSendFrom());
		ChessGame game = chessGamesHandler.getGameByUniqueHashId(webSocketUserObj.getUniqueActualGameHash());
		game.setEndDate(new Date());
		game.setEndingGameFENString(messageObj.getFen());
		ChessGamesHandler.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

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

		// save to DB
		usersRepository.save(user1);

		UserAccount user2 = usersRepository.findOneByUsername(messageObj.getSendTo());

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

			game.setWinnerName(messageObj.getSendFrom());
			game.setLoserName(messageObj.getSendTo());

			// winner -----------------------------------
			UserAccount winner = usersRepository.findOneByUsername(game.getWinnerName());

			Long winnerNumberOfWonGames = winner.getNumberOfWonChessGames();

			if (winnerNumberOfWonGames == null) {
				winner.setNumberOfWonChessGames(new Long(1));
			} else {
				winnerNumberOfWonGames++;
				winner.setNumberOfWonChessGames(winnerNumberOfWonGames);
			}

			usersRepository.save(winner);

			// looser ----------------------------------

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

	private synchronized Boolean userONEPlayWithUserTWO(GameUser fromUser, GameUser toUser) {
		log.debug("userONEPlayWithUserTWO()");

		if (fromUser != null && toUser != null && fromUser.getPlayNowWithUser().equals(toUser.getUsername())
				&& toUser.getPlayNowWithUser().equals(fromUser.getUsername())) {
			return true;
		} else {
			return false;
		}

	}

	private synchronized Boolean isUserPlayingWithAnyUser(GameUser user) {
		log.debug("isUserPlayingWithAnyUser()");

		if (user != null && user.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)
				&& user.getPlayNowWithUser() != null && !user.getPlayNowWithUser().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private synchronized void setUserComStatusIsDuringHandshakeSendMsgAndRefresh(GameMessage messageObj,
			String messageJsonString) {
		log.debug("setUserComStatusIsDuringHandshakeAndRefresh()");

		GameUser invitedUser = gameUsersRepository.getWebsocketUser(messageObj.getSendTo());

		if (invitedUser != null
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_DURING_HANDSHAKE)
				&& !invitedUser.getCommunicationStatus().equals(GameUserCommunicationStatus.IS_PLAYING)) {

			gameUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendFrom());
			gameUsersRepository.setComStatusIsDuringHandshake(messageObj.getSendTo());

			gameUsersRepository.setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());

			GameUser sendToObj = gameUsersRepository.getWebsocketUser(messageObj.getSendTo());

			messageObj.setSendToObj(sendToObj);

			GameUser sendFromObj = gameUsersRepository.getWebsocketUser(messageObj.getSendFrom());

			messageObj.setSendFromObj(sendFromObj);

			sendMessageToOneUser(messageObj, gson.toJson(messageObj));

			wsSesionsRepository.sendToAllConnectedSessionsActualParticipantList();
		} else {
			log.debug("invited user is already playing, is during handshake or is null");

			GameMessage tryLaterMsg = new GameMessage();
			tryLaterMsg.setType(GameMessageType.TRY_LATER);

			wsSesionsRepository.sendToSession(messageObj.getSendFrom(), "server", gson.toJson(tryLaterMsg));
		}

	}

	private synchronized void setUserComStatusIsPlayingAndRefresh(GameMessage messageObj) {
		log.debug("setUserComStatusIsPlayingAndRefresh()");

		String actualChessGameUUID = UUID.randomUUID().toString();

		gameUsersRepository.setComStatusIsPlaying(messageObj.getSendTo(), messageObj.getSendFrom());
		gameUsersRepository.setComStatusIsPlaying(messageObj.getSendFrom(), messageObj.getSendTo());

		GameUser sendToObj = gameUsersRepository.getWebsocketUser(messageObj.getSendTo());

		sendToObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendToObj(sendToObj);

		GameUser sendFromObj = gameUsersRepository.getWebsocketUser(messageObj.getSendFrom());

		sendFromObj.setUniqueActualGameHash(actualChessGameUUID);
		messageObj.setSendFromObj(sendFromObj);
		messageObj.setMoveStatus(ChessMoveStatus.WHITE_TO_MOVE);

		ChessGame chessGame = prepareAndReturnChessGameObjectAtGameStart(actualChessGameUUID, sendToObj, sendFromObj,
				messageObj);

		chessGamesHandler.addNewGame(chessGame);

		sendMessageToOneUser(messageObj, gson.toJson(messageObj));

		wsSesionsRepository.sendToAllConnectedSessionsActualParticipantList();

	}

	private synchronized ChessGame prepareAndReturnChessGameObjectAtGameStart(String actualChessGameUUID,
			GameUser sendToObj, GameUser sendFromObj, GameMessage messageObj) {
		log.debug("prepareAndReturnChessGameObjectAtGameStart()");

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

	private synchronized void setUserComStatusWaitForNewGameAndRefresh(GameMessage messageObj) {
		log.debug("setUserComStatusWaitForNewGameAndRefresh()");

		gameUsersRepository.setComStatusWaitForNewGame(messageObj.getSendFrom());
		gameUsersRepository.setComStatusWaitForNewGame(messageObj.getSendTo());
		gameUsersRepository.setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());

		wsSesionsRepository.sendToAllConnectedSessionsActualParticipantList();
	}

	private synchronized void sendMessageToOneUser(GameMessage message, String content) {
		log.debug("sendMessageToOneUser()");
		log.debug("typ wiadomosci : " + message.getType());
		log.debug("od usera " + message.getSendFrom() + " do usera " + message.getSendTo());

		String toUsername = message.getSendTo();
		String fromUsername = message.getSendFrom();
		if (toUsername != null && StringUtils.isNotEmpty(toUsername)) {

			wsSesionsRepository.sendToSession(toUsername, fromUsername, content);
		}
	}

}
