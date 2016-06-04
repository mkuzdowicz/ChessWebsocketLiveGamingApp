package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.props.Messages;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
public class UserPanelController {

	private UsersRepository usersRepository;
	private ChessGamesRepository chessGamesRepository;
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = Logger.getLogger(UserPanelController.class);

	@Autowired
	public UserPanelController(UsersRepository usersRepository, ChessGamesRepository chessGamesRepository,
			PasswordEncoder passwordEncoder) {
		this.usersRepository = usersRepository;
		this.chessGamesRepository = chessGamesRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@RequestMapping("/user/your-account")
	public ModelAndView getLoggedInUserDetails(String errorrMessage, String successMessage, Principal principal) {

		logger.debug("getLoggedInUserDetails()");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentUserLogin = auth.getName();

		UserAccount user = usersRepository.findOneByUsername(currentUserLogin);

		ModelAndView yourAccount = new ModelAndView("yourAccount");
		EditForm editForm = new EditForm();
		yourAccount.addObject("editForm", editForm);
		yourAccount.addObject("user", user);
		yourAccount.addObject("errorrMessage", errorrMessage);
		yourAccount.addObject("successMessage", successMessage);
		addBasicObjectsToModelAndView(yourAccount, principal);

		return yourAccount;
	}

	@RequestMapping(value = "/user/your-account", method = RequestMethod.POST)
	public ModelAndView sendEditUserDataForUserAccount(@Valid @ModelAttribute("editForm") EditForm editForm,
			BindingResult result, Principal principal) {
		logger.debug("sendEditUserDataForUserAccount()");

		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		Boolean changePasswordCheckBoxIsUnchecked = !changePasswordFlag;
		if (changePasswordCheckBoxIsUnchecked) {
			if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
				ModelAndView editFormSite = new ModelAndView("yourAccount");
				editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
				editFormSite.addObject("editForm", editForm);
				return editFormSite;
			}
		} else {
			if (result.hasErrors()) {
				ModelAndView editFormSite = new ModelAndView("yourAccount");
				editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
				editFormSite.addObject("editForm", editForm);
				return editFormSite;
			}
		}

		String userLogin = editForm.getUsername();
		String name = editForm.getName();
		String lastname = editForm.getLastname();

		String email = editForm.getEmail();
		String password = editForm.getPassword();
		String confirmPassword = editForm.getConfirmPassword();

		if (changePasswordFlag && !password.equals(confirmPassword)) {

			return getLoggedInUserDetails(Messages.getProperty("error.passwords.notequal"), null, principal);
		}

		UserAccount user = usersRepository.findOneByUsername(userLogin);

		if (!StringUtils.isBlank(name)) {
			user.setName(name);
		}
		if (!StringUtils.isBlank(lastname)) {
			user.setLastname(lastname);
		}

		if (changePasswordFlag) {
			try {
				String hashedPassword = passwordEncoder.encode(password);
				user.setPassword(hashedPassword);
			} catch (Exception e) {
				logger.debug(e);
			}
		}
		user.setEmail(email);
		usersRepository.save(user);

		return getLoggedInUserDetails(null, Messages.getProperty("success.user.edit"), principal);
	}

	@RequestMapping(value = "/user/your-chessgames", method = RequestMethod.GET)
	public ModelAndView userGamesSite(Principal principal) {
		logger.debug("userGamesSite()");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userLogin = auth.getName();

		ModelAndView userGamesSite = new ModelAndView("userGames");
		addBasicObjectsToModelAndView(userGamesSite, principal);
		List<ChessGame> userChessGames = chessGamesRepository.findAllByWhitePlayerNameOrBlackPlayerName(userLogin);

		UserAccount userInfo = usersRepository.findOneByUsername(userLogin);

		userGamesSite.addObject("userChessGames", userChessGames);
		userGamesSite.addObject("user", userInfo);

		return userGamesSite;
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
