package org.example.kickoff.config.auth;

import jakarta.security.enterprise.CallerPrincipal;

import org.example.kickoff.model.Person;
import org.example.kickoff.view.ActiveUser;

/**
 * @see KickoffIdentityStore
 * @see ActiveUser
 */
public class KickoffCallerPrincipal extends CallerPrincipal {

	private static final long serialVersionUID = 1L;
    private final Person person;

	public KickoffCallerPrincipal(Person person) {
		super(person.getEmail());
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

}