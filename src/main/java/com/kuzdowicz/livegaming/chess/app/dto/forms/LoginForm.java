package com.kuzdowicz.livegaming.chess.app.dto.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LoginForm {

	@NotNull(message = "please enter a login")
	@Size(min = 5, max = 12, message = "your login should be between 5 - 12 characters")
	private String username;

	@NotNull(message = "please enter a password")
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})", message = "password should have at least one digit, at least one capital letter, be between 8 - 30 characters")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginForm [username=" + username + ", password=" + password
				+ "]";
	}

}
