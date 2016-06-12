package com.kuzdowicz.livegaming.chess.app.dto.gaming;

import com.google.gson.GsonBuilder;

public class GameMessageDto {

	private String type;
	private String fen;
	private String sendTo;
	private String sendFrom;
	private String moveStatus;
	private LiveGamingUserDto sendToObj;
	private LiveGamingUserDto sendFromObj;
	private Boolean checkMate;
	private ChessMoveDto chessMove;
	private String whiteColUsername;
	private String blackColUsername;

	public GameMessageDto() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LiveGamingUserDto getSendToObj() {
		return sendToObj;
	}

	public void setSendToObj(LiveGamingUserDto sendToObj) {
		this.sendToObj = sendToObj;
	}

	public LiveGamingUserDto getSendFromObj() {
		return sendFromObj;
	}

	public void setSendFromObj(LiveGamingUserDto sendFromObj) {
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

	public ChessMoveDto getChessMove() {
		return chessMove;
	}

	public void setChessMove(ChessMoveDto chessMove) {
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

	public String actualJsonString() {
		return new GsonBuilder().create().toJson(this);
	}

}
