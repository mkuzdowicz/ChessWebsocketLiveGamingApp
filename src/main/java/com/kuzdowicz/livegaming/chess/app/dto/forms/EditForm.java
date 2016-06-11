package com.kuzdowicz.livegaming.chess.app.dto.forms;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EditForm {

	private String userId;

	private String username;

	@Size(max = 15, message = "name should be maximum 10 characters")
	private String name;

	@Size(max = 15, message = "lastname should be maximum 15 characters")
	private String lastname;

	@Pattern(regexp = "((?=.+@.+\\..+).{8,40})", message = "wrong email!")
	private String email;

	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})", message = "password should have at least one digit, at least one capital letter, be between 8 - 30 characters")
	private String password;

	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})", message = "password should have at least one digit, at least one capital letter, be between 8 - 30 characters")
	private String confirmPassword;

	private Boolean changePasswordFlag;

	private Boolean grantAdminAuthorities;

	private Boolean accountConfirmed;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Boolean getChangePasswordFlag() {
		return changePasswordFlag;
	}

	public void setChangePasswordFlag(Boolean changePasswordFlag) {
		this.changePasswordFlag = changePasswordFlag;
	}

	public Boolean getGrantAdminAuthorities() {
		return grantAdminAuthorities;
	}

	public void setGrantAdminAuthorities(Boolean grantAdminAuthorities) {
		this.grantAdminAuthorities = grantAdminAuthorities;
	}

	public Boolean getAccountConfirmed() {
		return accountConfirmed;
	}

	public void setAccountConfirmed(Boolean accountConfirmed) {
		this.accountConfirmed = accountConfirmed;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
