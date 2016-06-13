package com.kuzdowicz.livegaming.chess.app.livegaming.registries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.kuzdowicz.livegaming.chess.app.constants.ChessColor;
import com.kuzdowicz.livegaming.chess.app.constants.GameUserCommunicationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.GameMessageDto;
import com.kuzdowicz.livegaming.chess.app.dto.gaming.LiveGamingUserDto;

@Component
public class LiveGamingUsersRegistry {

	protected volatile static Map<String, LiveGamingUserDto> gameUsersMap = new ConcurrentHashMap<>();

	public synchronized void addWebsocketUser(LiveGamingUserDto gameUser) {
		gameUsersMap.put(gameUser.getUsername(), gameUser);
	}

	public synchronized LiveGamingUserDto getWebsocketUser(String username) {
		LiveGamingUserDto gameUser = gameUsersMap.get(username);
		return gameUser;
	}

	public synchronized void removeWebsocketUser(String username) {
		gameUsersMap.remove(username);
	}

	public synchronized void setUserComunicationStatus(String username, String status) {
		LiveGamingUserDto gameUser = gameUsersMap.get(username);
		gameUser.setCommunicationStatus(status);
	}

	public synchronized void setComStatusIsDuringHandshake(String username) {
		setUserComunicationStatus(username, GameUserCommunicationStatus.IS_DURING_HANDSHAKE);
	}

	public synchronized void setComStatusWaitForNewGame(String username) {
		LiveGamingUserDto gameUser = gameUsersMap.get(username);
		gameUser.setCommunicationStatus(GameUserCommunicationStatus.WAIT_FOR_NEW_GAME);
		gameUser.setPlayNowWithUser(null);
	}

	public void resetPlayersPairStateToWiatForNewGame(GameMessageDto messageObj) {
		setComStatusWaitForNewGame(messageObj.getSendFrom());
		setComStatusWaitForNewGame(messageObj.getSendTo());
		setChessPiecesColorForGamers(messageObj.getSendTo(), messageObj.getSendFrom());
	}

	public synchronized void setComStatusIsPlaying(String toUsername, String fromUsername) {
		LiveGamingUserDto gameUser = gameUsersMap.get(toUsername);
		gameUser.setCommunicationStatus(GameUserCommunicationStatus.IS_PLAYING);
		gameUser.setPlayNowWithUser(fromUsername);
	}

	public synchronized void setChessPiecesColorForGamers(String toUsername, String fromUsername) {
		LiveGamingUserDto invitingUser = gameUsersMap.get(fromUsername);
		LiveGamingUserDto recievingUser = gameUsersMap.get(toUsername);
		invitingUser.setChessColor(ChessColor.WHITE);
		recievingUser.setChessColor(ChessColor.BLACK);
	}

	public synchronized void resetChessPiecesColorForGamers(String toUsername, String fromUsername) {
		LiveGamingUserDto invitingUser = gameUsersMap.get(fromUsername);
		LiveGamingUserDto recievingUser = gameUsersMap.get(toUsername);
		invitingUser.setChessColor(null);
		recievingUser.setChessColor(null);
	}

	public synchronized Boolean userListNotContainsUsername(String username) {
		if (gameUsersMap.containsKey(username)) {
			return false;
		}
		return true;
	}

}
