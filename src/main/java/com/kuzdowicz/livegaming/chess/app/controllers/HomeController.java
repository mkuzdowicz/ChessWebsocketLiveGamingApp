package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	public ModelAndView home(Principal principal) {
		logger.info("homePage()");

		ModelAndView homePageModel = new ModelAndView("home");
		addBasicObjectsToModelAndView(homePageModel, principal);

		return homePageModel;
	}

	@RequestMapping(value = "/home/best-players", method = RequestMethod.GET)
	public ModelAndView bestPlayersSite(Principal principal) {
		logger.info("bestPlayersSite()");

		ModelAndView bestPlayers = new ModelAndView("bestPlayers");
		addBasicObjectsToModelAndView(bestPlayers, principal);
		List<UserAccount> bestPlayingUsers = repository.findAll();
		Gson gson = new Gson();
		bestPlayers.addObject("bestPlayersJson", gson.toJson(bestPlayingUsers));

		return bestPlayers;
	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView, Principal principal) {
		modelAndView.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
