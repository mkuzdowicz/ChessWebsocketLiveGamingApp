package com.kuzdowicz.livegaming.chess.app.controllers;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.props.Messages;

@Controller
public class PlayChessSitesController {

	private static final Logger logger = Logger
			.getLogger(PlayChessSitesController.class);

	@RequestMapping(value = "/play-chess-with-computer", method = RequestMethod.GET)
	public ModelAndView playChessWithComputer() {
		logger.info("playChessWithComputer()");

		ModelAndView playChessWithCompPageModel = new ModelAndView(
				"playChessWithComputer");
		addBasicObjectsToModelAndView(playChessWithCompPageModel);

		return playChessWithCompPageModel;
	}

	@RequestMapping(value = "/play-chess-with-user", method = RequestMethod.GET)
	public ModelAndView playChessWithUser() {
		logger.info("playChessWithUser()");

		ModelAndView playChessWithUserPageModel = new ModelAndView(
				"playChessWithUser");
		playChessWithUserPageModel.addObject("warning",
				Messages.getProperty("warning.play.with.others"));
		addBasicObjectsToModelAndView(playChessWithUserPageModel);

		return playChessWithUserPageModel;
	}

	private void addBasicObjectsToModelAndView(ModelAndView modelAndView) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		String userLogin = auth.getName();
		modelAndView.addObject("currentUserName", userLogin);

	}

}
