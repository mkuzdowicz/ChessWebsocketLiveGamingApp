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
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;
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
	public UserAccountCreationStatus createNewAccountAsUser(SignUpForm signUpForm) {

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

	private UserAccount createBasicUserFromSignUpForm(SignUpForm signUpForm) {
		UserAccount newUser = new UserAccount();
		newUser.setUsername(signUpForm.getUsername());
		newUser.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		newUser.setEmail(signUpForm.getEmail());
		newUser.setRegistrationDate(new Date());
		return newUser;

	}

	@Override
	public UserAccountCreationStatus createNewAccountAsAdmin(SignUpForm signUpForm) {
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
	public UserAccount editUser(UserAccount u) {
		return usersRepository.save(u);
	}

	@Override
	public List<UserAccount> all() {
		return usersRepository.findAll();
	}

	private boolean loginIsTaken(String login) {
		return getUserByLogin(login) != null ? true : false;
	}

	@Override
	public EditForm getUserByIdAndBindToEditFormDto(String userId) {
		UserAccount user = getUserByPK(userId);
		EditForm editForm = new EditForm();
		editForm.setUsername(user.getUsername());
		editForm.setUserId(user.getId());
		editForm.setEmail(user.getEmail());
		editForm.setName(user.getName());
		editForm.setLastname(user.getLastname());
		editForm.setAccountConfirmed(user.getIsRegistrationConfirmed());
		editForm.setGrantAdminAuthorities(user.getRole() == UserRoles.ADMIN.geNumericValue() ? true : false);
		return editForm;
	}

}
