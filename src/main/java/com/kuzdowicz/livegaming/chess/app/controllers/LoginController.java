package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.dto.forms.LoginForm;

@Controller
@PropertySource("classpath:messages.properties")
public class LoginController {

	private final Environment env;

	@Autowired
	public LoginController(Environment env) {
		this.env = env;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage(LoginForm loginForm, Principal principal) {
		ModelAndView loginPageModel = new ModelAndView("pages/public/login");
		loginPageModel.addObject("loginForm", loginForm);
		addBasicObjectsToModelAndView(loginPageModel, principal);
		return loginPageModel;
	}

	@RequestMapping("/fail")
	public ModelAndView getFailPage() {
		ModelAndView errorPage = new ModelAndView("pages/public/error");
		errorPage.addObject("errorMessage", env.getProperty("error.loginfailed"));
		return errorPage;
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}
}
