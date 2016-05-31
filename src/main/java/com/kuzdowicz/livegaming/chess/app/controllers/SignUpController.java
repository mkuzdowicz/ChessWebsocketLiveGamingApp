package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.forms.dto.SignUpForm;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.props.Messages;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;
import com.kuzdowicz.livegaming.chess.app.services.MailService;

@Controller
public class SignUpController {

	private UsersRepository usersRepository;
	private PasswordEncoder passwordEncoder;
	private MailService mailService;

	@Autowired
	public SignUpController(UsersRepository usersRepository, PasswordEncoder passwordEncoder, MailService mailService) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	// sign in
	@RequestMapping("/signup")
	public ModelAndView getSignUpForm(SignUpForm signUpFomr, String msg, Principal principa) {

		ModelAndView signUpSite = new ModelAndView("signup");
		if (msg != null) {
			signUpSite.addObject("errorMessage", msg);
		}
		signUpSite.addObject("signUpFomr", signUpFomr);
		addBasicObjectsToModelAndView(signUpSite, principa);

		return signUpSite;
	}

	@RequestMapping("/signup/account/creation")
	public ModelAndView getSiteAccountCreationInfo(String userCreationMsg, boolean created, String userMail,
			String userPassword, Principal principa) {

		ModelAndView accountCreationInfo = new ModelAndView("creatAccountMessage");
		accountCreationInfo.addObject("msg", userCreationMsg);
		accountCreationInfo.addObject("created", created);
		if (created) {
			accountCreationInfo.addObject("userMail", userMail);
		}
		addBasicObjectsToModelAndView(accountCreationInfo, principa);

		return accountCreationInfo;

	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView addUserAction(@Valid @ModelAttribute("signUpFomr") SignUpForm signUpFomr, BindingResult result,
			Principal principa) {

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

			return getSignUpForm(signUpFomr, Messages.getProperty("error.passwords.notequal"), principa);
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

			return getSignUpForm(signUpFomr, Messages.getProperty("error.login.exists"), principa);

		}

		mailService.sendRegistrationMail(userEmail, userLogin, randomHashString);

		return getSiteAccountCreationInfo(Messages.getProperty("success.user.created"), true, userEmail, userPassword,
				principa);

	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));

	}

}
