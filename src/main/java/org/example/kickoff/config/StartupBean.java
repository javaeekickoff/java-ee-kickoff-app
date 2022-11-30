package org.example.kickoff.config;

import static java.text.MessageFormat.format;
import static java.util.ResourceBundle.getBundle;
import static org.example.kickoff.model.Group.ADMIN;
import static org.omnifaces.util.Faces.getLocale;
import static org.omnifaces.utils.Lang.isEmpty;

import java.util.ResourceBundle;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;
import org.omnifaces.cdi.Startup;
import org.omnifaces.util.Messages;

@Startup
public class StartupBean {

	@Inject
	private PersonService personService;

	@PostConstruct
	public void init() {
		setupTestPersons();
		configureMessageResolver();
	}

	private void setupTestPersons() {
		if (!personService.findByEmail("admin@kickoff.example.org").isPresent()) {
			Person person = new Person();
			person.setFirstName("Test");
			person.setLastName("Admin");
			person.setEmail("admin@kickoff.example.org");
			personService.register(person, "passw0rd", ADMIN);
		}

		if (!personService.findByEmail("user@kickoff.example.org").isPresent()) {
			Person person = new Person();
			person.setFirstName("Test");
			person.setLastName("User");
			person.setEmail("person@kickoff.example.org");
			personService.register(person, "passw0rd");
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