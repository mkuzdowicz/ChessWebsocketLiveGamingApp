package com.kuzdowicz.livegaming.chess.app.models;

import org.springframework.stereotype.Component;

@Component
public class GameUser extends ChessAppUser {

	private long userIdInGameContext;
	private String playNowWithUser;
	private long numberOfScores;
	private String chessColor;
	private String communicationStatus;
	private String uniqueActualGameHash;

	public GameUser() {
	}

	public GameUser(String username) {
		this.username = username;
	}

	public long getUserIdInGameContext() {
		return userIdInGameContext;
	}

	public void setUserIdInGameContext(long userIdInGameContext) {
		this.userIdInGameContext = userIdInGameContext;
	}

	public String getChessColor() {
		return chessColor;
	}

	public String getPlayNowWithUser() {
		return playNowWithUser;
	}

	public void setPlayNowWithUser(String playNowWithUser) {
		this.playNowWithUser = playNowWithUser;
	}

	public void setChessColor(String chessColor) {
		this.chessColor = chessColor;
	}

	public String getUniqueActualGameHash() {
		return uniqueActualGameHash;
	}

	public void setUniqueActualGameHash(String uniqueActualGameHash) {
		this.uniqueActualGameHash = uniqueActualGameHash;
	}

	public long getNumberOfScores() {
		return numberOfScores;
	}

	public void setNumberOfScores(long numberOfScores) {
		this.numberOfScores = numberOfScores;
	}

	public String getCommunicationStatus() {
		return communicationStatus;
	}

	public void setCommunicationStatus(String communicationStatus) {
		this.communicationStatus = communicationStatus;
	}

	@Override
	public String toString() {
		return "GameUser [username=" + username + ",  userIdInGameContext="
				+ userIdInGameContext + ", playNowWithUser=" + playNowWithUser
				+ ", numberOfScores=" + numberOfScores + ", chessColor="
				+ chessColor + ", communicationStatus=" + communicationStatus
				+ ", uniqueActualGameHash=" + uniqueActualGameHash + "]";
	}

}
