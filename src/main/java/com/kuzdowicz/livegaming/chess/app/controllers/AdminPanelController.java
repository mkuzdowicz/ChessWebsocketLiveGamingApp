package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kuzdowicz.livegaming.chess.app.constants.FormActionMessageType;
import com.kuzdowicz.livegaming.chess.app.constants.UserAccountCreationStatus;
import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersAccountsRepository;
import com.kuzdowicz.livegaming.chess.app.services.UsersAccountsService;

@Controller
@PropertySource("classpath:messages.properties")
public class AdminPanelController {

	private static final Logger logger = LoggerFactory.getLogger(AdminPanelController.class);

	private final UsersAccountsRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final Environment env;
	private final UsersAccountsService usersAccountsService;

	@Autowired
	public AdminPanelController(UsersAccountsRepository usersRepository, PasswordEncoder passwordEncoder,
			Environment env, UsersAccountsService usersAccountsService) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
		this.usersAccountsService = usersAccountsService;
	}

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ModelAndView getAllUsers(Principal principal) {
		ModelAndView usersPage = new ModelAndView("pages/admin/users");
		usersPage.addObject("users", usersAccountsService.all());
		addBasicObjectsToModelAndView(usersPage, principal);
		return usersPage;
	}

	@RequestMapping(value = "/admin/users/show-edit-form", method = RequestMethod.POST)
	public ModelAndView showEditUserForm(@RequestParam("userId") String userId, EditForm editForm,
			FormActionResultMsgDto formActionMsg, Principal principal) {

		ModelAndView userDetailPage = new ModelAndView("pages/admin/editUser");
		if (StringUtils.isEmpty(editForm.getUsername())) {
			editForm = usersAccountsService.getUserByIdAndBindToEditFormDto(userId);
		}
		userDetailPage.addObject("editForm", editForm);
		userDetailPage.addObject("formActionMsg", formActionMsg);
		addBasicObjectsToModelAndView(userDetailPage, principal);
		return userDetailPage;
	}

	@RequestMapping(value = "/admin/users/edit-user", method = RequestMethod.POST)
	public ModelAndView sendEditUserData(@Valid @ModelAttribute("editForm") EditForm editForm, BindingResult result,
			Principal principal) {

		String password = editForm.getPassword();
		String confirmPassword = editForm.getConfirmPassword();
		String name = editForm.getName();
		String lastname = editForm.getLastname();
		String email = editForm.getEmail();
		Boolean adminFlag = editForm.getGrantAdminAuthorities();
		Boolean accountConfirmed = editForm.getAccountConfirmed();
		String userId = editForm.getUserId();

		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		ModelAndView editFormSite = new ModelAndView("pages/admin/editUser");

		if (changePasswordFlag && result.hasErrors()) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}

		if (changePasswordFlag && !password.equals(confirmPassword)) {

			return showEditUserForm(userId, editForm,
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), principal);
		}

		if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}

		UserAccount user = usersRepository.findOne(userId);
		user.setName(name);
		user.setLastname(lastname);
		if (changePasswordFlag) {
			user.setPassword(passwordEncoder.encode(password));
		}
		user.setEmail(email);
		user.setRole(adminFlag ? UserRoles.ADMIN.geNumericValue() : UserRoles.USER.geNumericValue());
		user.setIsRegistrationConfirmed(accountConfirmed);

		usersRepository.save(user);

		return showEditUserForm(user.getUsername(), editForm,
				FormActionResultMsgDto.createSuccessMsg(env.getProperty("success.user.edit")), principal);

	}

	@RequestMapping(value = "/admin/users/remove", method = RequestMethod.POST)
	public ModelAndView removeUser(@RequestParam("userId") String userId, Principal principal) {
		logger.debug("removeUser()");
		usersAccountsService.deleteUserByPK(userId);
		return getAllUsers(principal);
	}

	@RequestMapping(value = "/admin/users/addUser", method = RequestMethod.GET)
	public ModelAndView getAddUserForm(SignUpForm signUpForm, FormActionResultMsgDto formActionMsg,
			Principal principal) {
		logger.debug("getAddUserForm()");

		ModelAndView addUserPage = new ModelAndView("pages/admin/addUser");
		addUserPage.addObject("formActionMsg", formActionMsg);
		addUserPage.addObject("signUpForm", signUpForm);
		addBasicObjectsToModelAndView(addUserPage, principal);

		return addUserPage;
	}

	@RequestMapping(value = "/admin/users/addUser", method = RequestMethod.POST)
	public ModelAndView addUser(@Valid @ModelAttribute("signUpForm") SignUpForm signUpForm, BindingResult result,
			Principal principal) {
		logger.debug("addUser()");

		String userLogin = signUpForm.getUsername();
		String password = signUpForm.getPassword();
		String confirmPassword = signUpForm.getConfirmPassword();

		if (result.hasErrors()) {
			return getAddUserForm(signUpForm, new FormActionResultMsgDto(), principal);
		}
		if (!password.equals(confirmPassword)) {
			return getAddUserForm(//
					signUpForm, //
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), //
					principal);
		}

		UserAccountCreationStatus accCreationStatus = usersAccountsService.createNewAccountAsAdmin(signUpForm);
		if (accCreationStatus.equals(UserAccountCreationStatus.NOT_CREATED_LOGIN_TAKEN)) {
			String msg = "login " + userLogin + " allready exists!";
			return getAddUserForm(//
					signUpForm, //
					FormActionResultMsgDto.createErrorMsg(msg), //
					principal);
		}

		return getAddUserForm(//
				signUpForm, //
				FormActionResultMsgDto.createSuccessMsg(env.getProperty("success.user.created")), //
				principal);

	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
		mav.addObject("errorMsg", FormActionMessageType.ERROR);
		mav.addObject("successMsg", FormActionMessageType.SUCCESS);
	}

}
