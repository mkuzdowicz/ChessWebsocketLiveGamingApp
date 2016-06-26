package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.kuzdowicz.livegaming.chess.app.db.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;

@Controller
public class HomeController {

	private final UsersAccountsRepository repository;
	private final Gson gson;

	@Autowired
	public HomeController(UsersAccountsRepository repository, Gson gson) {
		this.repository = repository;
		this.gson = gson;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, Principal principal) {

		addBasicObjectsToModelAndView(model, principal);
		return "pages/public/home";
	}

	@RequestMapping(value = "/home/best-players", method = RequestMethod.GET)
	public String bestPlayersSite(Model model, Principal principal) {

		addBasicObjectsToModelAndView(model, principal);
		Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "numberOfWonChessGames"));
		List<UserAccount> bestPlayingUsers = repository.findAllWhereNumberOfWonChessGamesGt0(pageable);
		model.addAttribute("bestPlayersJson", gson.toJson(bestPlayingUsers));

		return "pages/public/bestPlayers";
	}

	private void addBasicObjectsToModelAndView(Model model, Principal principal) {
		model.addAttribute("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
