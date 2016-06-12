package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.FormActionMessageType;
import com.kuzdowicz.livegaming.chess.app.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditFormDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
import com.kuzdowicz.livegaming.chess.app.repositories.ChessGamesRepository;
import com.kuzdowicz.livegaming.chess.app.services.UsersAccountsService;

@Controller
@PropertySource("classpath:messages.properties")
@RequestMapping("/user")
public class UserPanelController {

	private final UsersAccountsService usersAccountsService;
	private final ChessGamesRepository chessGamesRepository;
	private final Environment env;

	@Autowired
	public UserPanelController(UsersAccountsService usersAccountsService, ChessGamesRepository chessGamesRepository,
			Environment env) {
		this.usersAccountsService = usersAccountsService;
		this.chessGamesRepository = chessGamesRepository;
		this.env = env;
	}

	@RequestMapping(value = "/your-account", method = RequestMethod.GET)
	public ModelAndView showLoggedInUserDetails(EditFormDto editForm, FormActionResultMsgDto formActionMsg,
			Principal principal) {

		ModelAndView yourAccount = new ModelAndView("pages/user/yourAccount");
		if (StringUtils.isEmpty(editForm.getUsername())) {
			editForm = usersAccountsService.getUserByLoginAndBindToEditFormDto(principal.getName());
		}
		yourAccount.addObject("editForm", editForm);
		yourAccount.addObject("formActionMsg", formActionMsg);
		addBasicObjectsToModelAndView(yourAccount, principal);
		return yourAccount;
	}

	@RequestMapping(value = "/your-account", method = RequestMethod.POST)
	public ModelAndView sendEditUserDataForUserAccount(@Valid @ModelAttribute("editForm") EditFormDto editForm,
			BindingResult result, Principal principal) {

		ModelAndView editFormSite = new ModelAndView("pages/user/yourAccount");
		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		String password = editForm.getPassword();
		String confirmPassword = editForm.getConfirmPassword();
		String userId = editForm.getUserId();

		if (changePasswordFlag && result.hasErrors()) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}
		if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}
		if (changePasswordFlag && !password.equals(confirmPassword)) {
			return showLoggedInUserDetails(editForm,
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), principal);
		}

		usersAccountsService.editUserAccoutnAsUser(editForm, userId);
		return showLoggedInUserDetails(//
				editForm, //
				FormActionResultMsgDto.createSuccessMsg(env.getProperty("success.user.edit")), //
				principal);

	}

	@RequestMapping(value = "/your-chessgames", method = RequestMethod.GET)
	public ModelAndView userGamesSite(Principal principal) {

		String userLogin = principal.getName();
		ModelAndView userGamesSite = new ModelAndView("pages/user/userGames");
		addBasicObjectsToModelAndView(userGamesSite, principal);
		List<ChessGame> userChessGames = chessGamesRepository.findAllByWhitePlayerNameOrBlackPlayerName(userLogin);
		userGamesSite.addObject("userChessGames", userChessGames);
		userGamesSite.addObject("user", usersAccountsService.getUserByLogin(principal.getName()));
		return userGamesSite;
	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
		mav.addObject("errorMsg", FormActionMessageType.ERROR);
		mav.addObject("successMsg", FormActionMessageType.SUCCESS);
	}

}
