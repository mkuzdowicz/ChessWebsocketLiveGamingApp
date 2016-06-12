package com.kuzdowicz.livegaming.chess.app.services;

import java.util.List;

import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.db.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditFormDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpFormDto;

public interface UsersAccountsService {

	UserAccount getUserByPK(String userId);

	UserAccount getUserByLogin(String login);

	UserAccountCreationStatus createNewAccountAsAdmin(SignUpFormDto signUpForm);

	UserAccountCreationStatus createNewAccountAsUser(SignUpFormDto signUpForm);

	void deleteUserByPK(String userId);

	List<UserAccount> all();

	EditFormDto getUserByIdAndBindToEditFormDto(String userId);
	
	EditFormDto getUserByLoginAndBindToEditFormDto(String login);

	UserAccount editUserAccoutnAsAdmin(EditFormDto editForm, String userIdToEdit);

	UserAccount editUserAccoutnAsUser(EditFormDto editForm, String userIdToEdit);
}
