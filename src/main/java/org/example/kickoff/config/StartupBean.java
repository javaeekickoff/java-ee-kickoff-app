package org.example.kickoff.config;

import static java.text.MessageFormat.format;
import static java.util.ResourceBundle.getBundle;
import static org.example.kickoff.model.Group.ADMIN;
import static org.omnifaces.util.Faces.getLocale;
import static org.omnifaces.utils.Lang.isEmpty;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.annotation.FacesConfig;
import javax.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.cdi.Startup;
import org.omnifaces.util.Messages;

@Startup
@FacesConfig
public class StartupBean {

	@Inject
	private UserService userService;

	@PostConstruct
	public void init() {
		setupTestUsers();
		configureMessageResolver();
	}

	private void setupTestUsers() {
		if (!userService.findByEmail("admin@kickoff.example.org").isPresent()) {
			User user = new User();
			user.setFirstName("Test");
			user.setLastName("Admin");
			user.setEmail("admin@kickoff.example.org");
			userService.register(user, "passw0rd", ADMIN);
		}

		if (!userService.findByEmail("user@kickoff.example.org").isPresent()) {
			User user = new User();
			user.setFirstName("Test");
			user.setLastName("User");
			user.setEmail("user@kickoff.example.org");
			userService.register(user, "passw0rd");
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