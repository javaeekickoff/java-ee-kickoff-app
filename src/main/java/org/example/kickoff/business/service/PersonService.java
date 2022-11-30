package org.example.kickoff.business.service;

import static java.util.Arrays.asList;
import static org.example.kickoff.model.Group.USER;
import static org.omnifaces.persistence.JPA.getOptionalSingleResult;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.example.kickoff.business.email.EmailService;
import org.example.kickoff.business.email.EmailTemplate;
import org.example.kickoff.business.email.EmailUser;
import org.example.kickoff.business.exception.DuplicateEntityException;
import org.example.kickoff.business.exception.InvalidPasswordException;
import org.example.kickoff.business.exception.InvalidTokenException;
import org.example.kickoff.business.exception.InvalidUsernameException;
import org.example.kickoff.model.Credentials;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.LoginToken.TokenType;
import org.example.kickoff.model.Person;
import org.omnifaces.persistence.service.BaseEntityService;

@Stateless
public class PersonService extends BaseEntityService<Long, Person> {

	private static final long DEFAULT_PASSWORD_RESET_EXPIRATION_TIME_IN_MINUTES = TimeUnit.HOURS.toMinutes(1);

	@Resource
	private SessionContext sessionContext;

	@Inject
	private LoginTokenService loginTokenService;

	@Inject
	private EmailService emailService;

	public void register(Person person, String password, Group... additionalGroups) {
		if (findByEmail(person.getEmail()).isPresent()) {
			throw new DuplicateEntityException();
		}

		person.getGroups().add(USER);
		person.getGroups().addAll(asList(additionalGroups));
		persist(person);
	    setPassword(person, password);
	}

	@Override
	public Person update(Person person) {
		Person existingPerson = manage(person);

		if (!person.getEmail().equals(existingPerson.getEmail())) { // Email changed.
			Optional<Person> otherPerson = findByEmail(person.getEmail());

			if (otherPerson.isPresent()) {
				if (!person.equals(otherPerson.get())) {
					throw new DuplicateEntityException();
				}
				else {
					// Since email verification status can be updated asynchronous, the DB status is leading.
					// Set the current person to whatever is already in the DB.
					person.setEmailVerified(otherPerson.get().isEmailVerified());
				}
			}
			else {
				person.setEmailVerified(false);
			}
		}

		return super.update(person);
	}

	public void updatePassword(Person person, String password) {
		Person existingPerson = manage(person);
		setPassword(existingPerson, password);
		super.update(existingPerson);
	}

	public void updatePassword(String loginToken, String password) {
		Optional<Person> person = findByLoginToken(loginToken, TokenType.RESET_PASSWORD);

		if (person.isPresent()) {
			updatePassword(person.get(), password);
			loginTokenService.remove(loginToken);
		}
	}

	public void requestResetPassword(String email, String ipAddress, String callbackUrlFormat) {
		Person person = findByEmail(email).orElseThrow(InvalidUsernameException::new);
		ZonedDateTime expiration = ZonedDateTime.now().plusMinutes(DEFAULT_PASSWORD_RESET_EXPIRATION_TIME_IN_MINUTES);
		String token = loginTokenService.generate(email, ipAddress, "Reset Password", TokenType.RESET_PASSWORD, expiration.toInstant());

		EmailTemplate emailTemplate = new EmailTemplate("resetPassword")
			.setToUser(new EmailUser(person))
			.setCallToActionURL(String.format(callbackUrlFormat, token));

		Map<String, Object> messageParameters = new HashMap<>();
		messageParameters.put("expiration", expiration);
		messageParameters.put("ip", ipAddress);

		emailService.sendTemplate(emailTemplate, messageParameters);
	}

	public Optional<Person> findByEmail(String email) {
		return getOptionalSingleResult(createNamedTypedQuery("Person.getByEmail")
			.setParameter("email", email));
	}

	public Optional<Person> findByLoginToken(String loginToken, TokenType type) {
		return getOptionalSingleResult(createNamedTypedQuery("Person.getByLoginToken")
			.setParameter("tokenHash", digest(loginToken, "SHA-256"))
			.setParameter("tokenType", type));
	}

	public Person getByEmail(String email) {
		return findByEmail(email).orElseThrow(InvalidUsernameException::new);
	}

	public Person getByEmailAndPassword(String email, String password) {
	    Person person = getByEmail(email);

	    if (!person.getCredentials().isValid(password)) {
	        throw new InvalidPasswordException();
	    }

	    return person;
	}

	public Person getByLoginToken(String loginToken, TokenType type) {
		return findByLoginToken(loginToken, type).orElseThrow(InvalidTokenException::new);
	}

	public Person getActivePerson() {
		return findByEmail(sessionContext.getCallerPrincipal().getName()).orElse(null);
	}

	public void setPassword(Person person, String password) {
		Person managedPerson = manage(person);
		Credentials credentials = managedPerson.getCredentials();

		if (credentials == null) {
			credentials = new Credentials();
			credentials.setPerson(managedPerson);
		}

		credentials.setPassword(password);
	}

}