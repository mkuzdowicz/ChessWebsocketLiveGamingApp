package com.kuzdowicz.livegaming.chess.app.controllers;

import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.forms.dto.SignUpForm;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.props.Messages;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;
import com.kuzdowicz.livegaming.chess.app.services.MailService;

@Controller
public class SignUpController {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private SignUpForm signUpFomr;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = Logger.getLogger(SignUpController.class);

	@Autowired
	private MailService mailService;

	// sign in
	@RequestMapping("/signup")
	public ModelAndView getSignUpForm(String msg) {

		ModelAndView signUpSite = new ModelAndView("signup");
		if (msg != null) {
			signUpSite.addObject("errorMessage", msg);
		}
		signUpSite.addObject("signUpFomr", signUpFomr);
		addBasicObjectsToModelAndView(signUpSite);

		return signUpSite;
	}

	@RequestMapping("/signup/account/creation")
	public ModelAndView getSiteAccountCreationInfo(String userCreationMsg, boolean created, String userMail,
			String userPassword) {

		ModelAndView accountCreationInfo = new ModelAndView("creatAccountMessage");
		accountCreationInfo.addObject("msg", userCreationMsg);
		accountCreationInfo.addObject("created", created);
		if (created) {
			accountCreationInfo.addObject("userMail", userMail);
		}
		addBasicObjectsToModelAndView(accountCreationInfo);

		return accountCreationInfo;

	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView addUserAction(@Valid @ModelAttribute("signUpFomr") SignUpForm signUpFomr,
			BindingResult result) {

		if (result.hasErrors()) {

			ModelAndView signUpSite = new ModelAndView("signup");
			signUpSite.addObject("signUpFomr", signUpFomr);
			return signUpSite;
		}

		String userLogin = signUpFomr.getUsername();
		String userEmail = signUpFomr.getEmail();
		String userPassword = signUpFomr.getPassword();
		String confirmPassword = signUpFomr.getConfirmPassword();

		// validation
		if (!userPassword.equals(confirmPassword)) {

			return getSignUpForm(Messages.getProperty("error.passwords.notequal"));
		}

		String hashPassword = passwordEncoder.encode(userPassword);

		UserAccount newUser = new UserAccount();
		newUser.setUsername(userLogin);
		newUser.setPassword(hashPassword);
		newUser.setRole(UserRoles.USER.geNumericValue());
		newUser.setEmail(userEmail);

		String randomHashString = UUID.randomUUID().toString();

		newUser.setRegistrationHashString(randomHashString);
		newUser.setIsRegistrationConfirmed(false);
		newUser.setRegistrationDate(new Date());

		UserAccount creationMessage = usersRepository.insert(newUser);

		if (creationMessage == null) {

			return getSignUpForm(Messages.getProperty("error.login.exists"));

		}

		mailService.sendRegistrationMail(userEmail, userLogin, randomHashString);

		return getSiteAccountCreationInfo(Messages.getProperty("success.user.created"), true, userEmail, userPassword);

	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String userLogin = auth.getName();
		modelAndView.addObject("currentUserName", userLogin);

	}

}
