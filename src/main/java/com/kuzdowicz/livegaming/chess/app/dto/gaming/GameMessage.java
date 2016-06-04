package com.kuzdowicz.livegaming.chess.app.dto.gaming;

public class GameMessage {

	private String type;

	private String fen;

	private String sendTo;

	private String sendFrom;

	private String moveStatus;

	private GameUser sendToObj;

	private GameUser sendFromObj;

	private Boolean checkMate;

	private ChessMove chessMove;

	private String whiteColUsername;

	private String blackColUsername;

	public GameMessage() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public GameUser getSendToObj() {
		return sendToObj;
	}

	public void setSendToObj(GameUser sendToObj) {
		this.sendToObj = sendToObj;
	}

	public GameUser getSendFromObj() {
		return sendFromObj;
	}

	public void setSendFromObj(GameUser sendFromObj) {
		this.sendFromObj = sendFromObj;
	}

	public Boolean getCheckMate() {
		return checkMate;
	}

	public void setCheckMate(Boolean checkMate) {
		this.checkMate = checkMate;
	}

	public String getMoveStatus() {
		return moveStatus;
	}

	public void setMoveStatus(String moveStatus) {
		this.moveStatus = moveStatus;
	}

	public String getSendFrom() {
		return sendFrom;
	}

	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public ChessMove getChessMove() {
		return chessMove;
	}

	public void setChessMove(ChessMove chessMove) {
		this.chessMove = chessMove;
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

}
