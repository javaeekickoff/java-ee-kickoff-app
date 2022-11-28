package org.example.kickoff.config.auth;

import org.example.kickoff.model.User;
import org.example.kickoff.view.ActiveUser;

import jakarta.security.enterprise.CallerPrincipal;

/**
 * @see KickoffIdentityStore
 * @see ActiveUser
 */
public class KickoffCallerPrincipal extends CallerPrincipal {

	private static final long serialVersionUID = 1L;
    private final User user;

	public KickoffCallerPrincipal(User user) {
		super(user.getEmail());
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}