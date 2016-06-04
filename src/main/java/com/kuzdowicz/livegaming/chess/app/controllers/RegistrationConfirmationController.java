package com.kuzdowicz.livegaming.chess.app.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.props.Messages;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
public class RegistrationConfirmationController {

	@Autowired
	private UsersRepository usersRepository;

	private Logger logger = Logger
			.getLogger(RegistrationConfirmationController.class);

	@RequestMapping(value = "registration/confirm/{hash}", method = RequestMethod.GET)
	public ModelAndView confirmEmailAccount(@PathVariable("hash") String hash) {
		logger.debug("confirmEmailAccount()");

		UserAccount user = usersRepository
				.findOneByRegistrationHashString(hash);

		if (user != null) {
			user.setIsRegistrationConfirmed(true);
			user.setRegistrationHashString("");
			usersRepository.save(user);
		} else {
			ModelAndView error = new ModelAndView("error");
			error.addObject("errorMessage",
					Messages.getProperty("error.registration.confirm.failed"));
			return error;
		}

		ModelAndView confirmRegistrationMsg = new ModelAndView(
				"confirmRegistrationMessage");
		confirmRegistrationMsg.addObject("username", user.getUsername());
		return confirmRegistrationMsg;
	}

}
