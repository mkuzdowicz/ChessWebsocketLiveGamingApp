package com.kuzdowicz.livegaming.chess.app.models;

public class GameUser {

	private String username;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
