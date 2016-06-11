package com.kuzdowicz.livegaming.chess.app.services;

import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.constants.UserAccountDeleteStatus;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;

public interface UsersAccountsService {

	UserAccount getUserByPK(String userId);

	UserAccount getUserByLogin(String login);

	UserAccountCreationStatus createNewAccount(SignUpForm signUpForm);
	
	UserAccountDeleteStatus deleteUser(UserAccount u);
	
	UserAccount editUser(UserAccount u);

}
