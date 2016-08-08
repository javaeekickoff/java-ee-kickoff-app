package org.example.kickoff.business;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.kickoff.jpa.JPA.getOptional;
import static org.example.kickoff.jpa.JPA.getOptionalSingleResult;
import static org.example.kickoff.model.Group.USERS;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.model.Credentials;
import org.example.kickoff.model.LoginToken;
import org.example.kickoff.model.LoginToken.TokenType;
import org.example.kickoff.model.User;

@Stateless
public class UserService {

	private static final int DEFAULT_SALT_LENGTH = 40;
	private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";

	@PersistenceContext
	private EntityManager entityManager;

	public void registerUser(User user, String password) {
		if (getByEmail(user.getEmail()) != null) {
			throw new ValidationException("Email address is already registered");
		}

		setCredentials(user, password);

		if (!user.getGroups().contains(USERS)) {
			user.getGroups().add(USERS);
		}

		entityManager.persist(user);
	}

	public void update(User user) {
		User otherUser = getByEmail(user.getEmail());
		if (otherUser != null && !user.equals(otherUser)) {
			throw new ValidationException("Email address is already registered");
		}

		entityManager.merge(user);
	}

	public void delete(User user) {
		if (!entityManager.contains(user)) {
			user = entityManager.merge(user);
		}

		entityManager.remove(user);
	}

	public void updatePassword(User user, String password) {
		setCredentials(user, password);

		entityManager.merge(user);
	}

	public User getByEmail(String email) {
		return getOptionalSingleResult(entityManager.createNamedQuery("User.getByEmail", User.class).setParameter("email", email));
	}
	
	public Optional<User> getUserByEmail(String email) {
		return getOptional(
			entityManager.createNamedQuery("User.getByEmail", User.class)
						 .setParameter("email", email));
	}
	
	public User getUserByEmailAndPassword(String email, String password) {
		User user = getOptionalSingleResult(
						entityManager.createNamedQuery("User.getByEmail", User.class)
									 .setParameter("email", email));

		if (user == null) {
			throw new InvalidCredentialsException();
		}

		Credentials credentials = user.getCredentials();
		if (credentials == null) {
			throw new InvalidCredentialsException();
		}

		byte[] passwordHash = digest(password, credentials.getSalt(), MESSAGE_DIGEST_ALGORITHM);

		if (!Arrays.equals(passwordHash, credentials.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}

		return user;
	}

	public User getUserByCredentials(String email, String password) {
		Credentials credentials = getOptionalSingleResult(entityManager.createNamedQuery("Credentials.getByEmail", Credentials.class)
				.setParameter("email", email));

		if (credentials == null) {
			throw new InvalidCredentialsException("Invalid username");
		}

		byte[] passwordHash = hashPassword(password, credentials.getSalt());

		if (!Arrays.equals(passwordHash, credentials.getPasswordHash())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		return credentials.getUser();
	}
	
	public String generateLoginToken(String email, String remoteAddress, String description, TokenType tokenType) {
		Instant expiration = Instant.now().plus(14, ChronoUnit.DAYS);
		return generateLoginToken(email, remoteAddress, description, tokenType, expiration);
	}

	public String generateLoginToken(String email, String remoteAddress, String description, TokenType tokenType, Instant expiration) {
		String loginToken = UUID.randomUUID().toString();

		User user = getUserByEmail(email).get();

		createLoginToken(loginToken, user, remoteAddress, expiration, description,
		                 tokenType);

		return loginToken;
	}
	
	public Optional<User> getUserByLoginToken(String loginToken, org.example.kickoff.model.LoginToken.TokenType type) {
		return getOptional(
				entityManager.createNamedQuery("User.getByLoginToken", User.class)
				             .setParameter("tokenHash", digest(loginToken, MESSAGE_DIGEST_ALGORITHM))
				             .setParameter("tokenType", type)
		);
	}
	
	public void removeLoginToken(String loginToken) {
		entityManager.createNamedQuery("LoginToken.removeLoginToken")
					 .setParameter("tokenHash", digest(loginToken, MESSAGE_DIGEST_ALGORITHM))
					 .executeUpdate();
	}

	public List<User> getUsers() {
		return entityManager.createNamedQuery("User.getAll", User.class)
							.getResultList();
	}

	private void setCredentials(User user, String password) {
		byte[] salt = generateSalt(DEFAULT_SALT_LENGTH);

		byte[] passwordHash = hashPassword(password, salt);

		Credentials credentials = user.getCredentials() != null ? user.getCredentials() : new Credentials();
		credentials.setPasswordHash(passwordHash);
		credentials.setSalt(salt);
		credentials.setUser(user);

		user.setCredentials(credentials);
	}

	private byte[] generateSalt(int saltLength) {
		byte[] salt = new byte[saltLength];

		ThreadLocalRandom.current().nextBytes(salt);

		return salt;
	}

	private byte[] hashPassword(String password, byte[] salt) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);

			messageDigest.update(salt);

			return messageDigest.digest(password.getBytes(UTF_8));
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
	}
	
	private LoginToken createLoginToken(String rawToken, User user, String ipAddress, Instant expiration, String description, TokenType tokenType) {
		org.example.kickoff.model.LoginToken loginToken = new LoginToken();
		loginToken.setTokenHash(digest(rawToken, MESSAGE_DIGEST_ALGORITHM));
		loginToken.setExpiration(Date.from(expiration));
		loginToken.setDescription(description);
		loginToken.setType(tokenType);

		loginToken.setUser(user);
		user.getLoginTokens().add(loginToken);

		loginToken.setIpAddress(ipAddress);

		return loginToken;
	}
}
