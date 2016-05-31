package com.kuzdowicz.livegaming.chess.app.constants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRoles {

	ADMIN(1, "ROLE_ADMIN", "ROLE_USER"), USER(2, "ROLE_USER");

	private int numericValue;

	private List<GrantedAuthority> userRoles;

	public static List<GrantedAuthority> getUserRoles(int id) {
		if (id == 1) {
			return ADMIN.userRoles;
		} else if (id == 2) {
			return USER.userRoles;
		}
		return null;

	}

	public List<GrantedAuthority> userRoles() {
		return userRoles;
	}

	public int geNumericValue() {
		return numericValue;
	}

	private UserRoles(int id, String role) {
		this.numericValue = id;
		List<GrantedAuthority> userRoles = new ArrayList<>();
		userRoles.add(new SimpleGrantedAuthority(role));
		this.userRoles = userRoles;
	}

	private UserRoles(int id, String role1, String role2) {
		this.numericValue = id;
		List<GrantedAuthority> userRoles = new ArrayList<>();
		userRoles.add(new SimpleGrantedAuthority(role1));
		userRoles.add(new SimpleGrantedAuthority(role2));
		this.userRoles = userRoles;
	}

	public static void main(String[] args) {
		System.out.println(UserRoles.getUserRoles(2));
	}

}
