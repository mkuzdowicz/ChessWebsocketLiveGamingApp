package com.kuzdowicz.livegaming.chess.app.services;

import java.util.List;

import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;

public interface UsersAccountsService {

	UserAccount getUserByPK(String userId);

	UserAccount getUserByLogin(String login);

	UserAccountCreationStatus createNewAccountAsAdmin(SignUpForm signUpForm);

	UserAccountCreationStatus createNewAccountAsUser(SignUpForm signUpForm);

	void deleteUserByPK(String userId);

	UserAccount editUser(UserAccount u);

	List<UserAccount> all();

	EditForm getUserByIdAndBindToEditFormDto(String userId);
}
