package com.kuzdowicz.livegaming.chess.app.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chessGames")
public class ChessGame implements Comparable<ChessGame> {

	@Id
	private String id;
	private long chessGameId;
	private Date beginDate;
	private Date endDate;
	private long gameDurationMillis;
	private String formattedGameDurationStr;
	private String endingGameFENString;
	private String winnerUsername;
	private String loserUsername;
	private String winnerColor;
	private int numberOfMoves;
	private String uniqueGameHash;
	private String whiteColUsername;
	private String blackColUsername;
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

	public long getChessGameId() {
		return chessGameId;
	}

	public void setChessGameId(long chessGameId) {
		this.chessGameId = chessGameId;
	}

	public String getWinnerUsername() {
		return winnerUsername;
	}

	public void setWinnerUsername(String winnerUsername) {
		this.winnerUsername = winnerUsername;
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

	public String getWhiteColUsername() {
		return whiteColUsername;
	}

	public void setWhiteColUsername(String whiteColUsername) {
		this.whiteColUsername = whiteColUsername;
	}

	public String getBlackColUsername() {
		return blackColUsername;
	}

	public void setBlackColUsername(String blackColUsername) {
		this.blackColUsername = blackColUsername;
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

	public String getLoserUsername() {
		return loserUsername;
	}

	public void setLoserUsername(String loserUsername) {
		this.loserUsername = loserUsername;
	}

	public List<ChessMove> getListOfMoves() {
		return listOfMoves;
	}

	public void setListOfMoves(List<ChessMove> listOfMoves) {
		this.listOfMoves = listOfMoves;
	}

	@Override
	public String toString() {
		return "ChessGame [id=" + id + ", chessGameId=" + chessGameId
				+ ", beginDate=" + beginDate + ", endDate=" + endDate
				+ ", gameDurationMillis=" + gameDurationMillis
				+ ", formattedGameDurationStr=" + formattedGameDurationStr
				+ ", endingGameFENString=" + endingGameFENString
				+ ", winnerUsername=" + winnerUsername + ", loserUsername="
				+ loserUsername + ", winnerColor=" + winnerColor
				+ ", numberOfMoves=" + numberOfMoves + ", uniqueGameHash="
				+ uniqueGameHash + ", whiteColUsername=" + whiteColUsername
				+ ", blackColUsername=" + blackColUsername + ", checkMate="
				+ checkMate + ", listOfMoves=" + listOfMoves + "]";
	}

	@Override
	public int compareTo(ChessGame game) {

		long compareChessGameId = game.getChessGameId();

		return (int) (this.chessGameId - compareChessGameId);
	}

}
