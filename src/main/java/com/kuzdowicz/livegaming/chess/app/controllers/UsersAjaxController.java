package com.kuzdowicz.livegaming.chess.app.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuzdowicz.livegaming.chess.app.models.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
public class UsersAjaxController {

	@Autowired
	private UsersRepository usersRepository;

	private static final Logger logger = Logger.getLogger(UsersAjaxController.class);

	@RequestMapping(value = "user/get-user-info-by-username", method = RequestMethod.GET)
	public @ResponseBody UserAccount getUserInfoByUsername(@RequestParam("username") String username) {
		logger.debug("getUserInfoById()");

		UserAccount user = usersRepository.findOneByUsername(username);

		return user;
	}

}
