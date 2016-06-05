package com.kuzdowicz.livegaming.chess.app.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserAccountNotConfirmed extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public UserAccountNotConfirmed(String msg) {
		super(msg);
	}

}
