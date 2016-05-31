package com.kuzdowicz.livegaming.chess.app.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private UserAccount userAccount;

	public UserDetailsImpl(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return UserRoles.getUserRoles(userAccount.getRole());
	}

	@Override
	public String getPassword() {

		return userAccount.getPassword();
	}

	@Override
	public String getUsername() {

		return userAccount.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

}
