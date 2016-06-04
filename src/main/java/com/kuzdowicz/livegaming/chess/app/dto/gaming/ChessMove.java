package com.kuzdowicz.livegaming.chess.app.dto.gaming;

public class ChessMove {

	private String color;
	private String flags;
	private String from;
	private String to;
	private String piece;
	private String san;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPiece() {
		return piece;
	}

	public void setPiece(String piece) {
		this.piece = piece;
	}

	public String getSan() {
		return san;
	}

	public void setSan(String san) {
		this.san = san;
	}

}
