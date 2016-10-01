package org.example.kickoff.config;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.ResourceBundle.getBundle;
import static org.example.kickoff.model.Group.ADMIN;
import static org.example.kickoff.model.Group.USER;
import static org.omnifaces.util.Faces.getLocale;
import static org.omnifaces.utils.Lang.isEmpty;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.example.kickoff.business.exception.InvalidUsernameException;
import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.cdi.Startup;
import org.omnifaces.util.Messages;

@Startup
public class StartupBean {

	@Inject
	private UserService userService;

	@PostConstruct
	public void init() {
		setupTestUsers();
		configureMessageResolver();
	}

	private void setupTestUsers() {
	 	try {
			userService.getByEmailAndPassword("admin@kickoff.example.org", "passw0rd");
		}
	 	catch (InvalidUsernameException e) {
			User user = new User();
			user.setFullName("Test Admin");
			user.setEmail("admin@kickoff.example.org");
			user.setGroups(asList(ADMIN, USER));
			userService.registerUser(user, "passw0rd");
		}

		try {
			userService.getByEmailAndPassword("user@kickoff.example.org", "passw0rd");
		}
		catch (InvalidUsernameException e) {
			User user = new User();
			user.setFullName("Test User");
			user.setEmail("user@kickoff.example.org");
			user.setGroups(asList(USER));
			userService.registerUser(user, "passw0rd");
		}
	}

	private void configureMessageResolver() {
		Messages.setResolver(new Messages.Resolver() {
			private static final String BASE_NAME = "org.example.kickoff.i18n.ApplicationBundle";

			@Override
			public String getMessage(String message, Object... params) {
				ResourceBundle bundle = getBundle(BASE_NAME, getLocale());
				if (bundle.containsKey(message)) {
					message = bundle.getString(message);
				}
				return isEmpty(params) ? message : format(message, params);
			}
		});
	}

}