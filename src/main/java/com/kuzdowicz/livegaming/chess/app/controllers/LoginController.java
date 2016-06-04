package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.dto.forms.LoginForm;
import com.kuzdowicz.livegaming.chess.app.props.Messages;

@Controller
public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage(LoginForm loginForm, Principal principal) {
		logger.debug("getLoginPage()");
		ModelAndView loginPageModel = new ModelAndView("login");
		loginPageModel.addObject("loginForm", loginForm);
		addBasicObjectsToModelAndView(loginPageModel, principal);

		return loginPageModel;
	}

	@RequestMapping("/fail")
	public ModelAndView getFailPage() {
		logger.debug("getFailPage()");

		ModelAndView errorPage = new ModelAndView("error");
		errorPage.addObject("errorMessage", Messages.getProperty("error.loginfailed"));

		return errorPage;

	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));

	}

}
