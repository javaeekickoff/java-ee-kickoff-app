package org.example.kickoff.business.service;

import static org.example.kickoff.model.Group.USER;
import static org.omnifaces.persistence.JPA.getOptional;
import static org.omnifaces.persistence.JPA.getOptionalSingleResult;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.business.exception.DuplicateEntityException;
import org.example.kickoff.business.exception.InvalidPasswordException;
import org.example.kickoff.business.exception.InvalidUsernameException;
import org.example.kickoff.model.Credentials;
import org.example.kickoff.model.LoginToken.TokenType;
import org.example.kickoff.model.User;
import org.omnifaces.persistence.model.dto.SortFilterPage;
import org.omnifaces.utils.collection.PartialResultList;

@Stateless
public class UserService extends BaseEntityService<Long, User> {

	private static final int DEFAULT_SALT_LENGTH = 40;
	private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private SessionContext sessionContext;

	@Inject
	private GenericEntityService genericEntityService;

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
		Optional<User> existingUser = getByEmail(user.getEmail());

		if (existingUser.isPresent() && !user.equals(existingUser.get())) {
			throw new InvalidUsernameException();
		}

		super.update(user);
	}

	public void updatePassword(User user, String password) {
		User existingUser = getByEmail(user.getEmail()).orElseThrow(InvalidUsernameException::new);
		setCredentials(existingUser, password);
		super.update(existingUser);
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

	public PartialResultList<User> getByPage(SortFilterPage sortFilterPage, boolean getCount) {
		return genericEntityService.getAllPagedAndSorted(User.class,
			(builder, query, tp) -> query.from(User.class),
			new HashMap<>(),
			sortFilterPage,
			getCount
		);
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