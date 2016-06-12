package com.kuzdowicz.livegaming.chess.app.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditFormDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpFormDto;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersAccountsRepository;

@Service
public class UsersAccountsServiceImpl implements UsersAccountsService {

	private final static Logger logger = LoggerFactory.getLogger(UsersAccountsServiceImpl.class);

	private final UsersAccountsRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final AccountRegistrationMailService mailService;

	@Autowired
	public UsersAccountsServiceImpl(UsersAccountsRepository usersRepository, PasswordEncoder passwordEncoder,
			AccountRegistrationMailService mailService) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	@Override
	public UserAccount getUserByPK(String userId) {
		return usersRepository.findOne(userId);
	}

	@Override
	public UserAccount getUserByLogin(String login) {
		return usersRepository.findOneByUsername(login);
	}

	@Transactional(rollbackFor = MessagingException.class)
	@Override
	public UserAccountCreationStatus createNewAccountAsUser(SignUpFormDto signUpForm) {

		if (loginIsTaken(signUpForm.getUsername())) {
			return UserAccountCreationStatus.NOT_CREATED_LOGIN_TAKEN;
		}

		UserAccount newUser = createBasicUserFromSignUpForm(signUpForm);

		String randomHashString = UUID.randomUUID().toString();
		newUser.setRegistrationHashString(randomHashString);
		newUser.setIsRegistrationConfirmed(false);
		newUser.setRole(UserRoles.USER.geNumericValue());
		try {
			mailService.sendRegistrationMail(newUser.getEmail(), newUser.getUsername(), randomHashString);
		} catch (MessagingException e) {
			logger.warn("exception occured: ", e);
			return UserAccountCreationStatus.FAIL;
		}
		usersRepository.insert(newUser);
		return UserAccountCreationStatus.CREATED;
	}

	private UserAccount createBasicUserFromSignUpForm(SignUpFormDto signUpForm) {
		UserAccount newUser = new UserAccount();
		newUser.setUsername(signUpForm.getUsername());
		newUser.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		newUser.setEmail(signUpForm.getEmail());
		newUser.setRegistrationDate(new Date());
		return newUser;
	}

	@Override
	public UserAccountCreationStatus createNewAccountAsAdmin(SignUpFormDto signUpForm) {
		if (loginIsTaken(signUpForm.getUsername())) {
			return UserAccountCreationStatus.NOT_CREATED_LOGIN_TAKEN;
		}
		UserAccount newUser = createBasicUserFromSignUpForm(signUpForm);
		Boolean adminFlag = signUpForm.getGrantAdminAuthorities();
		newUser.setRole(adminFlag ? UserRoles.ADMIN.geNumericValue() : UserRoles.USER.geNumericValue());
		newUser.setIsRegistrationConfirmed(true);

		usersRepository.insert(newUser);
		return UserAccountCreationStatus.CREATED;
	}

	@Override
	public void deleteUserByPK(String userId) {
		usersRepository.delete(getUserByPK(userId));
	}

	@Override
	public List<UserAccount> all() {
		return usersRepository.findAll();
	}

	private boolean loginIsTaken(String login) {
		return getUserByLogin(login) != null ? true : false;
	}

	@Override
	public EditFormDto getUserByIdAndBindToEditFormDto(String userId) {
		UserAccount user = getUserByPK(userId);
		EditFormDto editForm = new EditFormDto();
		editForm.setUsername(user.getUsername());
		editForm.setUserId(user.getId());
		editForm.setEmail(user.getEmail());
		editForm.setName(user.getName());
		editForm.setLastname(user.getLastname());
		editForm.setAccountConfirmed(user.getIsRegistrationConfirmed());
		editForm.setGrantAdminAuthorities(user.getRole() == UserRoles.ADMIN.geNumericValue() ? true : false);
		return editForm;
	}

	@Override
	public EditFormDto getUserByLoginAndBindToEditFormDto(String login) {
		logger.debug("getUserByLoginAndBindToEditFormDto()");
		UserAccount user = getUserByLogin(login);
		EditFormDto editForm = new EditFormDto();
		editForm.setUsername(user.getUsername());
		editForm.setUserId(user.getId());
		editForm.setEmail(user.getEmail());
		editForm.setName(user.getName());
		editForm.setLastname(user.getLastname());
		editForm.setAccountConfirmed(user.getIsRegistrationConfirmed());
		editForm.setGrantAdminAuthorities(user.getRole() == UserRoles.ADMIN.geNumericValue() ? true : false);
		return editForm;
	}

	@Override
	public UserAccount editUserAccoutnAsAdmin(EditFormDto editForm, String userIdToEdit) {
		logger.debug("editUserAccoutnAsAdmin()");
		UserAccount u = getUserByIdAndBindWithEditForm(editForm, userIdToEdit);
		Boolean setAsAdminFlag = editForm.getGrantAdminAuthorities();
		u.setRole(setAsAdminFlag ? UserRoles.ADMIN.geNumericValue() : UserRoles.USER.geNumericValue());
		u.setIsRegistrationConfirmed(editForm.getAccountConfirmed());
		usersRepository.save(u);
		return u;
	}

	@Override
	public UserAccount editUserAccoutnAsUser(EditFormDto editForm, String userIdToEdit) {
		UserAccount u = getUserByIdAndBindWithEditForm(editForm, userIdToEdit);
		usersRepository.save(u);
		return u;
	}

	private UserAccount getUserByIdAndBindWithEditForm(EditFormDto editForm, String userId) {
		logger.debug("getUserByIdAndBindWithEditForm()");
		UserAccount userToEdit = getUserByPK(userId);
		userToEdit.setName(editForm.getName());
		userToEdit.setLastname(editForm.getLastname());
		userToEdit.setEmail(editForm.getEmail());
		if (editForm.getChangePasswordFlag()) {
			userToEdit.setPassword(passwordEncoder.encode(editForm.getPassword()));
		}
		return userToEdit;
	}

}
