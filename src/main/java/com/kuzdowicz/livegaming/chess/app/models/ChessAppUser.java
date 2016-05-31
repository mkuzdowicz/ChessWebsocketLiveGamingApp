package com.kuzdowicz.livegaming.chess.app.models;

public abstract class ChessAppUser {

	protected String username;

	public ChessAppUser() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
