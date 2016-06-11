package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.FormActionMessageType;
import com.kuzdowicz.livegaming.chess.app.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
@PropertySource("classpath:messages.properties")
public class UserPanelController {

	private static final Logger logger = LoggerFactory.getLogger(UserPanelController.class);

	private final UsersRepository usersRepository;
	private final ChessGamesRepository chessGamesRepository;
	private final PasswordEncoder passwordEncoder;
	private final Environment env;

	@Autowired
	public UserPanelController(UsersRepository usersRepository, ChessGamesRepository chessGamesRepository,
			PasswordEncoder passwordEncoder, Environment env) {
		this.usersRepository = usersRepository;
		this.chessGamesRepository = chessGamesRepository;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
	}

	@RequestMapping(value = "/user/your-account", method = RequestMethod.GET)
	public ModelAndView getLoggedInUserDetails(EditForm editForm, FormActionResultMsgDto formActionMsg,
			Principal principal) {

		UserAccount user = usersRepository.findOneByUsername(principal.getName());
		ModelAndView yourAccount = new ModelAndView("pages/user/yourAccount");
		yourAccount.addObject("editForm", editForm);
		yourAccount.addObject("user", user);
		yourAccount.addObject("formActionMsg", formActionMsg);
		addBasicObjectsToModelAndView(yourAccount, principal);

		return yourAccount;
	}

	@RequestMapping(value = "/user/your-account", method = RequestMethod.POST)
	public ModelAndView sendEditUserDataForUserAccount(@Valid @ModelAttribute("editForm") EditForm editForm,
			BindingResult result, Principal principal) {

		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		Boolean changePasswordCheckBoxIsUnchecked = !changePasswordFlag;
		if (changePasswordCheckBoxIsUnchecked) {
			if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
				ModelAndView editFormSite = new ModelAndView("pages/user/yourAccount");
				editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
				editFormSite.addObject("editForm", editForm);
				return editFormSite;
			}
		} else {
			if (result.hasErrors()) {
				ModelAndView editFormSite = new ModelAndView("pages/user/yourAccount");
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

			return getLoggedInUserDetails(//
					editForm, //
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), //
					principal);
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
				logger.warn("exception occured: ", e);
			}
		}
		user.setEmail(email);
		usersRepository.save(user);

		return getLoggedInUserDetails(//
				editForm, //
				FormActionResultMsgDto.createSuccessMsg(env.getProperty("success.user.edit")), //
				principal);

	}

	@RequestMapping(value = "/user/your-chessgames", method = RequestMethod.GET)
	public ModelAndView userGamesSite(Principal principal) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userLogin = auth.getName();

		ModelAndView userGamesSite = new ModelAndView("pages/user/userGames");
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
		mav.addObject("errorMsg", FormActionMessageType.ERROR);
		mav.addObject("successMsg", FormActionMessageType.SUCCESS);
	}

}
