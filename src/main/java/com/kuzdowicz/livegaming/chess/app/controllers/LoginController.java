package com.kuzdowicz.livegaming.chess.app.controllers;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.forms.dto.LoginForm;
import com.kuzdowicz.livegaming.chess.app.props.Messages;

@Controller
public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage() {
		logger.debug("getLoginPage()");
		ModelAndView loginPageModel = new ModelAndView("login");
		addBasicObjectsToModelAndView(loginPageModel);

		return loginPageModel;
	}

	@RequestMapping("/fail")
	public ModelAndView getFailPage() {
		logger.debug("getFailPage()");

		ModelAndView errorPage = new ModelAndView("error");
		errorPage.addObject("errorMessage", Messages.getProperty("error.loginfailed"));

		return errorPage;

	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String userLogin = auth.getName();
		modelAndView.addObject("currentUserName", userLogin);
		LoginForm loginForm = new LoginForm();
		modelAndView.addObject("loginForm", loginForm);

	}

}
