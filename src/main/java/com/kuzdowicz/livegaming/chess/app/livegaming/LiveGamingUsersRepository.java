package com.kuzdowicz.livegaming.chess.app.livegaming;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.kuzdowicz.livegaming.chess.app.constants.ChessColor;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.models.GameUser;

@Component
public class LiveGamingUsersRepository {

	private final Logger logger = Logger.getLogger(LiveGamingUsersRepository.class);

	protected volatile static Map<String, GameUser> gameUsersMap = new ConcurrentHashMap<>();

	public synchronized void addWebsocketUser(GameUser gameUser) {

		gameUsersMap.put(gameUser.getUsername(), gameUser);
		logger.info("user: " + gameUser + " added to live game repository");
	}

	public synchronized GameUser getWebsocketUser(String username) {
		GameUser gameUser = gameUsersMap.get(username);
		return gameUser;
	}

	public synchronized void removeWebsocketUser(String username) {
		GameUser gameUser = gameUsersMap.remove(username);
		logger.info("user: " + gameUser + " removed from live game repository");
	}

	public synchronized void setUserComunicationStatus(String username, String status) {
		GameUser gameUser = gameUsersMap.get(username);
		gameUser.setCommunicationStatus(status);
	}

	public synchronized void setComStatusIsDuringHandshake(String username) {
		logger.debug("setComStatusIsPlaying()");

		setUserComunicationStatus(username, GameUserCommunicationStatus.IS_DURING_HANDSHAKE);
	}

	public synchronized void setComStatusWaitForNewGame(String username) {
		logger.debug("setComStatusWaitForNewGame()");

		GameUser gameUser = gameUsersMap.get(username);
		gameUser.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);
		gameUser.setPlayNowWithUser(null);
	}

	public synchronized void setComStatusIsPlaying(String toUsername, String fromUsername) {
		logger.debug("setComStatusIsPlaying()");

		GameUser gameUser = gameUsersMap.get(toUsername);
		gameUser.setCommunicationStatus(GameUserCommunicationStatus.IS_PLAYING);
		gameUser.setPlayNowWithUser(fromUsername);

	}

	public synchronized void setChessPiecesColorForGamers(String toUsername, String fromUsername) {
		logger.debug("setChessPiecesColorForGamers()");

		GameUser invitingUser = gameUsersMap.get(fromUsername);
		GameUser recievingUser = gameUsersMap.get(toUsername);

		invitingUser.setChessColor(ChessColor.WHITE);
		recievingUser.setChessColor(ChessColor.BLACK);

	}

	public synchronized void resetChessPiecesColorForGamers(String toUsername, String fromUsername) {
		logger.debug("setChessPiecesColorForGamers()");

		GameUser invitingUser = gameUsersMap.get(fromUsername);
		GameUser recievingUser = gameUsersMap.get(toUsername);

		invitingUser.setChessColor(null);
		recievingUser.setChessColor(null);

	}

	public void printOutUsersList() {
		logger.info("printOutUsersList()");
		for (String key : gameUsersMap.keySet()) {
			System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(gameUsersMap.get(key)));
		}

	}

	public synchronized Boolean userListNotContainsUsername(String username) {
		if (gameUsersMap.containsKey(username)) {
			return false;
		}
		return true;
	}

}
