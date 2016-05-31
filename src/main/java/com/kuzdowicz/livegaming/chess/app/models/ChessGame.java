package com.kuzdowicz.livegaming.chess.app.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chessGames")
public class ChessGame {

	@Id
	private String id;
	private Date beginDate;
	private Date endDate;
	private long gameDurationMillis;
	private String formattedGameDurationStr;
	private String endingGameFENString;
	private String winnerName;
	private String loserName;
	private String winnerColor;
	private int numberOfMoves;
	private String uniqueGameHash;
	private String whitePlayerName;
	private String blackPlayerName;
	private Boolean checkMate;
	private List<ChessMove> listOfMoves;

	public ChessGame() {
		listOfMoves = new ArrayList<>();
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndingGameFENString() {
		return endingGameFENString;
	}

	public void setEndingGameFENString(String endingGameFENString) {
		this.endingGameFENString = endingGameFENString;
	}

	public String getWinnerColor() {
		return winnerColor;
	}

	public Boolean getCheckMate() {
		return checkMate;
	}

	public void setCheckMate(Boolean checkMate) {
		this.checkMate = checkMate;
	}

	public long getGameDurationMillis() {
		return gameDurationMillis;
	}

	public void setGameDurationMillis(long gameDurationMillis) {
		this.gameDurationMillis = gameDurationMillis;
	}

	public String getFormattedGameDurationStr() {
		return formattedGameDurationStr;
	}

	public void setFormattedGameDurationStr(String formattedGameDurationStr) {
		this.formattedGameDurationStr = formattedGameDurationStr;
	}

	public String getUniqueGameHash() {
		return uniqueGameHash;
	}

	public void setUniqueGameHash(String uniqueGameHash) {
		this.uniqueGameHash = uniqueGameHash;
	}

	public void setWinnerColor(String winnerColor) {
		this.winnerColor = winnerColor;
	}

	public int getNumberOfMoves() {
		return numberOfMoves;
	}

	public void setNumberOfMoves(int numberOfMoves) {
		this.numberOfMoves = numberOfMoves;
	}

	public List<ChessMove> getListOfMoves() {
		return listOfMoves;
	}

	public void setListOfMoves(List<ChessMove> listOfMoves) {
		this.listOfMoves = listOfMoves;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	public String getLoserName() {
		return loserName;
	}

	public void setLoserName(String loserName) {
		this.loserName = loserName;
	}

	public String getWhitePlayerName() {
		return whitePlayerName;
	}

	public void setWhitePlayerName(String whitePlayerName) {
		this.whitePlayerName = whitePlayerName;
	}

	public String getBlackPlayerName() {
		return blackPlayerName;
	}

	public void setBlackPlayerName(String blackPlayerName) {
		this.blackPlayerName = blackPlayerName;
	}

}
