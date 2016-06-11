package com.kuzdowicz.livegaming.chess.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.exceptions.UserAccountNotConfirmed;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersAccountsRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UsersAccountsRepository usersRepository;

	@Autowired
	public UserDetailsServiceImpl(UsersAccountsRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserAccount user = usersRepository.findOneByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException(username + " login not found");
		}
		
		if(!user.getIsRegistrationConfirmed()){
			throw new UserAccountNotConfirmed(username + " not confirmed");
		}

		return new UserDetailsImpl(user);
	}

}
