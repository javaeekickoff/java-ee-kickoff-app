package org.example.kickoff.business;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.kickoff.jpa.JPA.getOptionalSingleResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.model.Credentials;
import org.example.kickoff.model.User;

@Stateless
public class UserService {

	private static final int DEFAULT_SALT_LENGTH = 40;

	@PersistenceContext
	private EntityManager entityManager;

	public void registerUser(User user, String password) {
		setCredentials(user, password);

		entityManager.persist(user);
	}

	public void updatePassword(User user, String password) {
		setCredentials(user, password);

		entityManager.merge(user);
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
	
	public User getUserByLoginToken(String loginToken) {
		User user = getOptionalSingleResult(entityManager.createNamedQuery("User.getByLoginToken", User.class).setParameter("loginToken", loginToken));
		
		if (user == null) {
			throw new InvalidCredentialsException("Invalid token");
		}
		
		return user;
	}
	
	public String generateLoginToken(String email) {
		
		String loginToken = UUID.randomUUID().toString();
		
		getOptionalSingleResult(
			entityManager.createNamedQuery("Credentials.getByEmail", Credentials.class).setParameter("email", email)
		).getUser().setLoginToken(loginToken);
		
		return loginToken;
	}
	
	public void removeLoginToken(String email) {
		getOptionalSingleResult(
			entityManager.createNamedQuery("Credentials.getByEmail", Credentials.class).setParameter("email", email)
		).getUser().setLoginToken(null);
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
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			messageDigest.update(salt);

			return messageDigest.digest(password.getBytes(UTF_8));
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
	}
}
