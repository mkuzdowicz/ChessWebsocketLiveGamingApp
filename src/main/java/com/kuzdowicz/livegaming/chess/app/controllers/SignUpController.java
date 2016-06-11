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

import com.kuzdowicz.livegaming.chess.app.constants.UserCreatedStatus;
import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
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

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView getSignUpForm(SignUpForm signUpFomr, FormActionResultMsgDto formActionMsg, Principal principa) {

		ModelAndView signUpSite = new ModelAndView("pages/public/signup");
		signUpSite.addObject("formActionMsg", formActionMsg);
		signUpSite.addObject("signUpFomr", signUpFomr);
		addBasicObjectsToModelAndView(signUpSite, principa);
		return signUpSite;
	}

	@RequestMapping(value = "/signup/account/creation", method = RequestMethod.GET)
	public ModelAndView getSiteAccountCreationInfo(String userCreationMsg, boolean created, String userMail,
			String userPassword, Principal principa) {

		ModelAndView accountCreatedPage = new ModelAndView("pages/public/accountCreatedMsgPage");

		accountCreatedPage.addObject("userMail", userMail);
		addBasicObjectsToModelAndView(accountCreatedPage, principa);
		return accountCreatedPage;
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
			FormActionResultMsgDto formActionMsg = FormActionResultMsgDto
					.createErrorMsg(env.getProperty("error.passwords.notequal"));
			return getSignUpForm(signUpFomr, formActionMsg, principa);
		}

		int creationStatus = createAccount(signUpFomr, principa);
		if (creationStatus == UserCreatedStatus.FAIL) {
			FormActionResultMsgDto formActionMsg = FormActionResultMsgDto
					.createErrorMsg(env.getProperty("error.confirmation.mail"));
			return getSignUpForm(signUpFomr, formActionMsg, principa);
		}
		ModelAndView accountCreatedPage = new ModelAndView("pages/public/accountCreatedMsgPage");
		accountCreatedPage.addObject("userMail", signUpFomr.getEmail());
		addBasicObjectsToModelAndView(accountCreatedPage, principa);

		return accountCreatedPage;
	}

	@Transactional
	private int createAccount(SignUpForm signUpFomr, Principal principa) {

		String userLogin = signUpFomr.getUsername();
		String userEmail = signUpFomr.getEmail();
		String userPassword = signUpFomr.getPassword();

		UserAccount newUser = new UserAccount();
		newUser.setUsername(userLogin);
		newUser.setPassword(passwordEncoder.encode(userPassword));
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
			return UserCreatedStatus.FAIL;
		}

		usersRepository.insert(newUser);
		return UserCreatedStatus.CREATED;
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
