package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
public class HomeController {

	private final UsersRepository repository;
	private final Gson gson;

	@Autowired
	public HomeController(UsersRepository repository, Gson gson) {
		this.repository = repository;
		this.gson = gson;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Principal principal) {

		ModelAndView homePageModel = new ModelAndView("pages/public/home");
		addBasicObjectsToModelAndView(homePageModel, principal);
		return homePageModel;
	}

	@RequestMapping(value = "/home/best-players", method = RequestMethod.GET)
	public ModelAndView bestPlayersSite(Principal principal) {

		ModelAndView bestPlayers = new ModelAndView("pages/public/bestPlayers");
		addBasicObjectsToModelAndView(bestPlayers, principal);
		Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "numberOfWonChessGames"));
		List<UserAccount> bestPlayingUsers = repository.findAllWhereNumberOfWonChessGamesGt0(pageable);
		bestPlayers.addObject("bestPlayersJson", gson.toJson(bestPlayingUsers));

		return bestPlayers;
	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView, Principal principal) {
		modelAndView.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
