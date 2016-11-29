package org.example.kickoff.business.service;

import static org.example.kickoff.model.Group.USER;
import static org.omnifaces.persistence.JPA.getOptional;
import static org.omnifaces.persistence.JPA.getOptionalSingleResult;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.business.email.EmailService;
import org.example.kickoff.business.email.EmailTemplate;
import org.example.kickoff.business.email.EmailUser;
import org.example.kickoff.business.exception.DuplicateEntityException;
import org.example.kickoff.business.exception.InvalidPasswordException;
import org.example.kickoff.business.exception.InvalidUsernameException;
import org.example.kickoff.model.Credentials;
import org.example.kickoff.model.LoginToken.TokenType;
import org.example.kickoff.model.User;

@Stateless
public class UserService extends BaseEntityService<Long, User> {

	private static final int DEFAULT_SALT_LENGTH = 40;
	private static final long DEFAULT_PASSWORD_RESET_EXPIRATION_TIME_IN_MINUTES = TimeUnit.HOURS.toMinutes(1);
	private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private SessionContext sessionContext;

	@Inject
	private LoginTokenService loginTokenService;

	@Inject
	private EmailService emailService;

	public void registerUser(User user, String password) {
		if (getByEmail(user.getEmail()).isPresent()) {
			throw new DuplicateEntityException();
		}

		setCredentials(user, password);

		if (!user.getGroups().contains(USER)) {
			user.getGroups().add(USER);
		}

		super.save(user);
	}

	@Override
	public void update(User user) {
		User existingUser = get(user);

		if (!user.getEmail().equals(existingUser.getEmail())) { // Email changed.
			Optional<User> otherUser = getByEmail(user.getEmail());

			if (otherUser.isPresent()) {
				if (!user.equals(otherUser.get())) {
					throw new DuplicateEntityException();
				}
				else {
					// Since email verification status can be updated asynchronous, the DB status is leading.
					// Set the current user to whatever is already in the DB.
					user.setEmailVerified(otherUser.get().isEmailVerified());
				}
			}
			else {
				user.setEmailVerified(false);
			}
		}

		super.update(user);
	}

	public void updatePassword(User user, String password) {
		User existingUser = get(user);
		setCredentials(existingUser, password);
		super.update(existingUser);
	}

	public void updatePassword(String loginToken, String password) {
		User user = getByLoginToken(loginToken, TokenType.RESET_PASSWORD);

		if (user != null) {
			updatePassword(user, password);
			loginTokenService.remove(loginToken);
		}
	}

	public void requestResetPassword(String email, String ipAddress, String callbackUrlFormat) {
		User user = getByEmail(email).orElseThrow(InvalidUsernameException::new);
		ZonedDateTime expiration = ZonedDateTime.now().plusMinutes(DEFAULT_PASSWORD_RESET_EXPIRATION_TIME_IN_MINUTES);
		String token = loginTokenService.generate(email, ipAddress, "Reset Password", TokenType.RESET_PASSWORD, expiration.toInstant());

		EmailTemplate emailTemplate = new EmailTemplate("resetPassword")
			.setToUser(new EmailUser(user))
			.setCallToActionURL(String.format(callbackUrlFormat, token));

		Map<String, Object> messageParameters = new HashMap<>();
		messageParameters.put("expiration", expiration);
		messageParameters.put("ip", ipAddress);

		emailService.sendTemplate(emailTemplate, messageParameters);
	}

	public Optional<User> getByEmail(String email) {
		return getOptional(entityManager
			.createNamedQuery("User.getByEmail", User.class)
			.setParameter("email", email));
	}

	public User getByLoginToken(String loginToken, TokenType type) {
		return getOptionalSingleResult(entityManager
			.createNamedQuery("User.getByLoginToken", User.class)
			.setParameter("tokenHash", digest(loginToken, MESSAGE_DIGEST_ALGORITHM))
			.setParameter("tokenType", type));
	}

	public User getByEmailAndPassword(String email, String password) {
		User user = getByEmail(email).orElseThrow(InvalidUsernameException::new);
		Credentials credentials = user.getCredentials();

		if (credentials == null) {
			throw new InvalidUsernameException();
		}

		byte[] passwordHash = digest(password, credentials.getSalt(), MESSAGE_DIGEST_ALGORITHM);

		if (!Arrays.equals(passwordHash, credentials.getPasswordHash())) {
			throw new InvalidPasswordException();
		}

		return user;
	}

	public User getActiveUser() {
		return getByEmail(sessionContext.getCallerPrincipal().getName()).orElse(null);
	}

	private static void setCredentials(User user, String password) {
		byte[] salt = generateSalt(DEFAULT_SALT_LENGTH);
		byte[] passwordHash = digest(password, salt, MESSAGE_DIGEST_ALGORITHM);

		Credentials credentials = user.getCredentials();

		if (credentials == null) {
			credentials = new Credentials();
			credentials.setUser(user);
			user.setCredentials(credentials);
		}

		credentials.setPasswordHash(passwordHash);
		credentials.setSalt(salt);
	}

	private static byte[] generateSalt(int saltLength) {
		byte[] salt = new byte[saltLength];
		ThreadLocalRandom.current().nextBytes(salt);
		return salt;
	}

}