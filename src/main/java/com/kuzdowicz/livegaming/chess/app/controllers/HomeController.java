package com.kuzdowicz.livegaming.chess.app.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
public class HomeController {

	private static final Logger logger = Logger.getLogger(HomeController.class);

	private UsersRepository repository;
	
	
	@Autowired
	public HomeController(UsersRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		logger.info("homePage()");

		ModelAndView homePageModel = new ModelAndView("home");
		addBasicObjectsToModelAndView(homePageModel);

		return homePageModel;
	}

	@RequestMapping(value = "/home/best-players", method = RequestMethod.GET)
	public ModelAndView bestPlayersSite() {
		logger.info("bestPlayersSite()");

		ModelAndView bestPlayers = new ModelAndView("bestPlayers");
		addBasicObjectsToModelAndView(bestPlayers);
		List<UserAccount> bestPlayingUsers = repository.findAll();
		Gson gson = new Gson();
		bestPlayers.addObject("bestPlayersJson", gson.toJson(bestPlayingUsers));

		return bestPlayers;
	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		String userLogin = auth.getName();
		modelAndView.addObject("currentUserName", userLogin);

	}

}
