package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;
import com.kuzdowicz.livegaming.chess.app.services.MailService;

@Controller
@PropertySource("classpath:messages.properties")
public class SignUpController {

	private final static Logger logger = LoggerFactory.getLogger(SignUpController.class);

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final Environment env;

	@Autowired
	public SignUpController(UsersRepository usersRepository, PasswordEncoder passwordEncoder, MailService mailService,
			Environment env) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.env = env;
	}

	@RequestMapping("/signup")
	public ModelAndView getSignUpForm(SignUpForm signUpFomr, String msg, Principal principa) {

		ModelAndView signUpSite = new ModelAndView("pages/public/signup");
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

		ModelAndView accountCreationInfo = new ModelAndView("pages/public/creatAccountMessage");
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
			ModelAndView signUpSite = new ModelAndView("pages/public/signup");
			signUpSite.addObject("signUpFomr", signUpFomr);
			return signUpSite;
		}
		String userPassword = signUpFomr.getPassword();
		String confirmPassword = signUpFomr.getConfirmPassword();

		if (!userPassword.equals(confirmPassword)) {
			return getSignUpForm(signUpFomr, env.getProperty("error.passwords.notequal"), principa);
		}
		return createAccount(signUpFomr, principa);
	}

	@Transactional
	private ModelAndView createAccount(SignUpForm signUpFomr, Principal principa) {

		String userLogin = signUpFomr.getUsername();
		String userEmail = signUpFomr.getEmail();
		String userPassword = signUpFomr.getPassword();
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

		try {
			mailService.sendRegistrationMail(userEmail, userLogin, randomHashString);
		} catch (Exception e) {
			logger.warn("exception occured: ", e);
			return getSignUpForm(signUpFomr, env.getProperty("error.confirmation.mail"), principa);
		}

		usersRepository.insert(newUser);
		return getSiteAccountCreationInfo(env.getProperty("success.user.created"), true, userEmail, userPassword,
				principa);
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
