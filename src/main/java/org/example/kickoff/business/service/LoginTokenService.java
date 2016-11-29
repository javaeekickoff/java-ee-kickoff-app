package org.example.kickoff.business.service;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.UUID.randomUUID;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.time.Instant;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.business.exception.InvalidUsernameException;
import org.example.kickoff.model.LoginToken;
import org.example.kickoff.model.LoginToken.TokenType;
import org.example.kickoff.model.User;

@Stateless
public class LoginTokenService extends BaseEntityService<Long, LoginToken> {

	private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private UserService userService;

	public String generate(String email, String ipAddress, String description, TokenType tokenType) {
		Instant expiration = now().plus(14, DAYS);
		return generate(email, ipAddress, description, tokenType, expiration);
	}

	public String generate(String email, String ipAddress, String description, TokenType tokenType, Instant expiration) {
		String rawToken = randomUUID().toString();
		User user = userService.getByEmail(email).orElseThrow(InvalidUsernameException::new);

		LoginToken loginToken = new LoginToken();
		loginToken.setTokenHash(digest(rawToken, MESSAGE_DIGEST_ALGORITHM));
		loginToken.setExpiration(expiration);
		loginToken.setDescription(description);
		loginToken.setType(tokenType);
		loginToken.setIpAddress(ipAddress);
		loginToken.setUser(user);
		user.getLoginTokens().add(loginToken);
		return rawToken;
	}

	public void remove(String loginToken) {
		entityManager.createNamedQuery("LoginToken.remove")
					 .setParameter("tokenHash", digest(loginToken, MESSAGE_DIGEST_ALGORITHM))
					 .executeUpdate();
	}

	public void removeExpired() {
		entityManager.createNamedQuery("LoginToken.removeExpired")
					 .executeUpdate();
	}

}