package com.kuzdowicz.livegaming.chess.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersAccountsRepository;

@Controller
@PropertySource("classpath:messages.properties")
public class RegistrationConfirmationController {

	private final UsersAccountsRepository usersRepository;
	private final Environment env;

	@Autowired
	public RegistrationConfirmationController(UsersAccountsRepository usersRepository, Environment env) {
		this.usersRepository = usersRepository;
		this.env = env;
	}

	@RequestMapping(value = "registration/confirm/{hash}", method = RequestMethod.GET)
	public ModelAndView confirmEmailAccount(@PathVariable("hash") String hash) {

		UserAccount user = usersRepository.findOneByRegistrationHashString(hash);
		if (user != null) {
			user.setIsRegistrationConfirmed(true);
			user.setRegistrationHashString("");
			usersRepository.save(user);
		} else {
			ModelAndView error = new ModelAndView("pages/public/error");
			error.addObject("errorMessage", env.getProperty("error.registration.confirm.failed"));
			return error;
		}

		ModelAndView confirmRegistrationMsg = new ModelAndView("pages/public/confirmRegistrationMessage");
		confirmRegistrationMsg.addObject("username", user.getUsername());
		return confirmRegistrationMsg;
	}

}
