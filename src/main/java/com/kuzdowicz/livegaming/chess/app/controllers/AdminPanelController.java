package com.kuzdowicz.livegaming.chess.app.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;
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

import com.kuzdowicz.livegaming.chess.app.constants.UserRoles;
import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.dto.forms.EditForm;
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

		List<UserAccount> users = usersRepository.findAll();
		ModelAndView usersPage = new ModelAndView("pages/admin/users");
		usersPage.addObject("users", users);
		addBasicObjectsToModelAndView(usersPage, principal);
		return usersPage;
	}

	@RequestMapping(value = "admin/users/editUser", method = RequestMethod.GET)
	public ModelAndView showEditUserForm(@RequestParam("login") String login, String errorMessage,
			String successMessage, Principal principal) {

		UserAccount user = usersRepository.findOneByUsername(login);

		ModelAndView userDetailPage = new ModelAndView("pages/admin/editUser");
		EditForm editForm = new EditForm();
		userDetailPage.addObject("editForm", editForm);
		userDetailPage.addObject("user", user);
		userDetailPage.addObject("errorMessage", errorMessage);
		userDetailPage.addObject("successMessage", successMessage);
		addBasicObjectsToModelAndView(userDetailPage, principal);
		return userDetailPage;
	}

	@RequestMapping(value = "admin/users/editUser", method = RequestMethod.POST)
	public ModelAndView sendEditUserData(@Valid @ModelAttribute("editForm") EditForm editForm, BindingResult result,
			Principal principal) {

		Boolean changePasswordFlag = editForm.getChangePasswordFlag();
		Boolean changePasswordCheckBoxIsUnchecked = !changePasswordFlag;
		ModelAndView editFormSite = new ModelAndView("pages/admin/editUser");
		if (changePasswordCheckBoxIsUnchecked) {
			if (result.hasFieldErrors("email") || result.hasFieldErrors("name") || result.hasFieldErrors("lastname")) {
				editFormSite.addObject("changePasswordCheckBoxIsChecked", changePasswordFlag);
				editFormSite.addObject("editForm", editForm);
				return editFormSite;
			}
		} else {
			if (result.hasErrors()) {
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

			return showEditUserForm(userLogin, env.getProperty("error.passwords.notequal"), null, principal);
		}

		UserAccount user = usersRepository.findOneByUsername(userLogin);

		if (user != null) {

			if (!StringUtils.isBlank(name)) {
				user.setName(name);
			}
			if (!StringUtils.isBlank(lastname)) {
				user.setLastname(lastname);
			}

			if (changePasswordFlag) {
				try {
					String hashedPassword = passwordEncoder.encode(password).toString();
					user.setPassword(hashedPassword);
				} catch (Exception e) {
					logger.warn("exception occured: ", e);
				}
			}
			user.setEmail(email);

			Boolean adminFlag = editForm.getGrantAdminAuthorities();

			if (adminFlag) {
				user.setRole(UserRoles.ADMIN.geNumericValue());
			} else {
				user.setRole(UserRoles.USER.geNumericValue());
			}

			user.setEmail(email);
			usersRepository.save(user);

			return showEditUserForm(user.getUsername(), null, env.getProperty("success.user.edit"), principal);

		} else {

			return showEditUserForm(null, env.getProperty("error.fatal.error"), null, principal);
		}
	}

	@RequestMapping(value = "admin/users/remove", method = RequestMethod.POST)
	public ModelAndView removeUser(@RequestParam("username") String username, Principal principal) {
		logger.debug("removeUser()");

		UserAccount user = usersRepository.findOneByUsername(username);
		usersRepository.delete(user);

		return getAllUsers(principal);
	}

	@RequestMapping(value = "/admin/users/addUser", method = RequestMethod.GET)
	public ModelAndView getAddUserForm(String errorMsg, String successMsg, Principal principal) {
		logger.debug("getAddUserForm()");

		ModelAndView addUserPage = new ModelAndView("pages/admin/addUser");
		addUserPage.addObject("errorMessage", errorMsg);
		addUserPage.addObject("successMsg", successMsg);
		SignUpForm signUpForm = new SignUpForm();
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

		// validation
		if (!plaintextPassword.equals(confirmPassword)) {

			return getAddUserForm(env.getProperty("error.passwords.notequal"), null, principal);
		}

		UserAccount user = new UserAccount();
		user.setUsername(userLogin);

		if (adminFlag) {
			user.setRole(UserRoles.ADMIN.geNumericValue());
		} else {
			user.setRole(UserRoles.USER.geNumericValue());
		}

		try {
			String hashedPassword = passwordEncoder.encode(plaintextPassword);
			user.setPassword(hashedPassword);
		} catch (Exception e) {
			logger.warn("exception occured: ", e);
		}

		user.setEmail(email);
		user.setIsRegistrationConfirmed(true);
		user.setRegistrationDate(new Date());

		UserAccount updateResult = usersRepository.insert(user);

		if (updateResult != null) {
			return getAddUserForm(null, env.getProperty("success.user.created"), principal);
		} else {
			return getAddUserForm("login " + userLogin + " allready exists!", null, principal);
		}

	}

	private void addBasicObjectsToModelAndView(ModelAndView mav, Principal principal) {
		mav.addObject("currentUserName",
				Optional.ofNullable(principal).filter(p -> p != null).map(p -> p.getName()).orElse(""));
	}

}
