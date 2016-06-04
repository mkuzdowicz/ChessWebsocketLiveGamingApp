package com.kuzdowicz.livegaming.chess.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UsersRepository usersRepository;

	@Autowired
	public UserDetailsServiceImpl(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserAccount user = usersRepository.findOneByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException(username + " login not found");
		}

		return new UserDetailsImpl(user);
	}

}
