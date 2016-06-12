package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpFormDto;
import com.kuzdowicz.livegaming.chess.app.services.UsersAccountsService;

@Controller
@PropertySource("classpath:messages.properties")
public class SignUpController {

	private final UsersAccountsService usersAccountsService;
	private final Environment env;

	@Autowired
	public SignUpController(UsersAccountsService usersAccountsService, Environment env) {
		this.usersAccountsService = usersAccountsService;
		this.env = env;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView getSignUpForm(SignUpFormDto signUpForm,
			FormActionResultMsgDto formActionMsg, Principal principa) {

		ModelAndView signUpSite = new ModelAndView("pages/public/signup");
		signUpSite.addObject("formActionMsg", formActionMsg);
		signUpSite.addObject("signUpForm", signUpForm);
		addBasicObjectsToModelAndView(signUpSite, principa);
		return signUpSite;
	}

	private ModelAndView accountCreatedInfoPage(String userMail, Principal principa) {

		ModelAndView accountCreatedPage = new ModelAndView("pages/public/accountCreatedMsgPage");
		accountCreatedPage.addObject("userMail", userMail);
		addBasicObjectsToModelAndView(accountCreatedPage, principa);
		return accountCreatedPage;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView addUserAction(@Valid @ModelAttribute("signUpForm") SignUpFormDto signUpForm,
			BindingResult result, Principal principal) {

		String userPassword = signUpForm.getPassword();
		String confirmPassword = signUpForm.getConfirmPassword();

		if (result.hasErrors()) {
			return getSignUpForm(signUpForm, FormActionResultMsgDto.empty(), principal);
		}

		if (!userPassword.equals(confirmPassword)) {
			FormActionResultMsgDto formActionMsg = FormActionResultMsgDto
					.createErrorMsg(env.getProperty("error.passwords.notequal"));
			return getSignUpForm(signUpForm, formActionMsg, principal);
		}

		UserAccountCreationStatus creationStatus = usersAccountsService.createNewAccountAsUser(signUpForm);
		if (creationStatus.equals(UserAccountCreationStatus.NOT_CREATED_LOGIN_TAKEN)) {
			String msg = "login " + signUpForm.getUsername() + " allready exists!";
			FormActionResultMsgDto formActionMsg = FormActionResultMsgDto.createErrorMsg(msg);
			return getSignUpForm(signUpForm, formActionMsg, principal);
		}

		if (creationStatus.equals(UserAccountCreationStatus.FAIL)) {
			FormActionResultMsgDto formActionMsg = FormActionResultMsgDto
					.createErrorMsg(env.getProperty("error.confirmation.mail"));
			return getSignUpForm(signUpForm, formActionMsg, principal);
		}

		return accountCreatedInfoPage(signUpForm.getEmail(), principal);
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
