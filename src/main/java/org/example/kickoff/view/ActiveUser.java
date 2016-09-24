package org.example.kickoff.view;

import javax.enterprise.inject.Typed;

import org.example.kickoff.model.User;

@Typed
public class ActiveUser {

	private User activeUser;

	public ActiveUser() {
		// C'tor for proxy.
	}

	public ActiveUser(User user) {
		activeUser = user;
	}

	public boolean isPresent() {
		return activeUser != null;
	}

	public Long getId() {
		return isPresent() ? activeUser.getId() : null;
	}

	public boolean hasRole(String role) {
		return isPresent() && activeUser.getRolesAsStream().anyMatch(r -> r.name().equals(role));
	}

}