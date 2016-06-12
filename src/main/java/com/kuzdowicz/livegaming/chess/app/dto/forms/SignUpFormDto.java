package com.kuzdowicz.livegaming.chess.app.dto.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignUpFormDto {

	@NotNull(message = "please enter a login")
	@Size(min = 5, max = 12, message = "your login should be between 5 - 12 characters")
	private String username;

	@NotNull(message = "please enter your email")
	@Pattern(regexp = "((?=.+@.+\\..+).{8,40})", message = "wrong email!")
	private String email;

	@NotNull(message = "please enter a password")
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})", message = "password should have at least one digit, at least one capital letter, be between 8 - 30 characters")
	private String password;

	@NotNull(message = "please enter a password")
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})", message = "password should have at least one digit, at least one capital letter, be between 8 - 30 characters")
	private String confirmPassword;

	private Boolean grantAdminAuthorities;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Boolean getGrantAdminAuthorities() {
		return grantAdminAuthorities;
	}

	public void setGrantAdminAuthorities(Boolean grantAdminAuthorities) {
		this.grantAdminAuthorities = grantAdminAuthorities;
	}

	@Override
	public String toString() {
		return "SignUpForm [username=" + username + ", email=" + email + ", password=" + password + ", confirmPassword="
				+ confirmPassword + ", grantAdminAuthorities=" + grantAdminAuthorities + "]";
	}

}
