package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

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

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
import com.kuzdowicz.livegaming.chess.app.dto.forms.FormActionResultMsgDto;
import com.kuzdowicz.livegaming.chess.app.dto.forms.SignUpForm;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Controller
@PropertySource("classpath:messages.properties")
public class AdminPanelController {

	private static final Logger logger = LoggerFactory.getLogger(AdminPanelController.class);

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final Environment env;

	@Autowired
	public AdminPanelController(UsersRepository usersRepository, PasswordEncoder passwordEncoder, Environment env) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
	}

	@RequestMapping(value = "admin/users", method = RequestMethod.GET)
	public ModelAndView getAllUsers(Principal principal) {
		ModelAndView usersPage = new ModelAndView("pages/admin/users");
		usersPage.addObject("users", usersRepository.findAll());
		addBasicObjectsToModelAndView(usersPage, principal);
		return usersPage;
	}

	@RequestMapping(value = "admin/users/editUser", method = RequestMethod.GET)
	public ModelAndView showEditUserForm(@RequestParam("login") String login, EditForm editForm,
			FormActionResultMsgDto formActionMsg, Principal principal) {

		UserAccount user = usersRepository.findOneByUsername(login);
		ModelAndView userDetailPage = new ModelAndView("pages/admin/editUser");
		userDetailPage.addObject("editForm", editForm);
		userDetailPage.addObject("user", user);
		userDetailPage.addObject("formActionMsg", formActionMsg);
		addBasicObjectsToModelAndView(userDetailPage, principal);
		return userDetailPage;
	}

	@RequestMapping(value = "admin/users/editUser", method = RequestMethod.POST)
	public ModelAndView sendEditUserData(@Valid @ModelAttribute("editForm") EditForm editForm, BindingResult result,
			Principal principal) {

		String password = editForm.getPassword();
		String confirmPassword = editForm.getConfirmPassword();
		String userLogin = editForm.getUsername();
		String name = editForm.getName();
		String lastname = editForm.getLastname();
		String email = editForm.getEmail();
		Boolean adminFlag = editForm.getGrantAdminAuthorities();

		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		ModelAndView editFormSite = new ModelAndView("pages/admin/editUser");

		if (changePasswordFlag && result.hasErrors()) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}

		if (changePasswordFlag && !password.equals(confirmPassword)) {

			return showEditUserForm(userLogin, editForm,
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), principal);
		}

		if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
			editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
			editFormSite.addObject("editForm", editForm);
			return editFormSite;
		}

		UserAccount user = usersRepository.findOneByUsername(userLogin);
		if (user == null) {
			return showEditUserForm(null, new EditForm(),
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.fatal.error")), principal);
		}
		user.setName(name);
		user.setLastname(lastname);
		if (changePasswordFlag) {
			user.setPassword(passwordEncoder.encode(password));
		}
		user.setEmail(email);
		user.setRole(adminFlag ? UserRoles.ADMIN.geNumericValue() : UserRoles.USER.geNumericValue());
		
		usersRepository.save(user);

		return showEditUserForm(user.getUsername(), editForm,
				FormActionResultMsgDto.createSucessMsg(env.getProperty("success.user.edit")), principal);

	}

	@RequestMapping(value = "admin/users/remove", method = RequestMethod.POST)
	public ModelAndView removeUser(@RequestParam("username") String username, Principal principal) {
		logger.debug("removeUser()");

		UserAccount user = usersRepository.findOneByUsername(username);
		usersRepository.delete(user);
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

	@RequestMapping(value = "admin/users/addUser", method = RequestMethod.POST)
	public ModelAndView addUser(@Valid @ModelAttribute("signUpForm") SignUpForm signUpForm, BindingResult result,
			Principal principal) {
		logger.debug("addUser()");

		if (result.hasErrors()) {
			ModelAndView addUserPage = new ModelAndView("pages/admin/addUser");
			addUserPage.addObject("signUpForm", signUpForm);
			return addUserPage;
		}

		String userLogin = signUpForm.getUsername();
		String plaintextPassword = signUpForm.getPassword();
		String confirmPassword = signUpForm.getConfirmPassword();
		Boolean adminFlag = signUpForm.getGrantAdminAuthorities();
		String email = signUpForm.getEmail();

		if (!plaintextPassword.equals(confirmPassword)) {
			return getAddUserForm(//
					signUpForm, //
					FormActionResultMsgDto.createErrorMsg(env.getProperty("error.passwords.notequal")), //
					principal);
		}

		UserAccount user = new UserAccount();
		user.setUsername(userLogin);
		user.setRole(adminFlag ? UserRoles.ADMIN.geNumericValue() : UserRoles.USER.geNumericValue());
		user.setPassword(passwordEncoder.encode(plaintextPassword));
		user.setEmail(email);
		user.setIsRegistrationConfirmed(true);
		user.setRegistrationDate(new Date());

		UserAccount updateResult = usersRepository.insert(user);

		if (updateResult != null) {
			return getAddUserForm(//
					signUpForm, //
					FormActionResultMsgDto.createErrorMsg(env.getProperty("success.user.created")), //
					principal);

		} else {
			String msg = "login " + userLogin + " allready exists!";
			return getAddUserForm(//
					signUpForm, //
					FormActionResultMsgDto.createErrorMsg(msg), //
					principal);
		}

	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
