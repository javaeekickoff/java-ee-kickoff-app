package org.example.kickoff.config.auth;

import javax.security.enterprise.CallerPrincipal;

import org.example.kickoff.model.User;

/**
 * @see KickoffIdentityStore
 * @see ActiveUser
 */
public class KickoffCallerPrincipal extends CallerPrincipal {

	private final User user;

	public KickoffCallerPrincipal(User user) {
		super(user.getEmail());
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}