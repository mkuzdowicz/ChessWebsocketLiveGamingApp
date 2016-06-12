package com.kuzdowicz.livegaming.chess.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuzdowicz.livegaming.chess.app.db.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.db.repositories.UsersAccountsRepository;

@Controller
public class UsersAjaxController {

	private final UsersAccountsRepository usersRepository;

	@Autowired
	public UsersAjaxController(UsersAccountsRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@RequestMapping(value = "user/get-user-info-by-username", method = RequestMethod.GET)
	public @ResponseBody UserAccount getUserInfoByUsername(@RequestParam("username") String username) {
		return usersRepository.findOneByUsername(username);
	}

}
