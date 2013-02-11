package org.example.kickoff.plumbing.jaspic.dto;

import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordAuthenticator;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordProvider;

public class Delegators {

	private final UsernamePasswordAuthenticator authenticator;
	private final UsernamePasswordProvider provider;

	public Delegators(UsernamePasswordAuthenticator authenticator, UsernamePasswordProvider loginBean) {
		this.authenticator = authenticator;
		this.provider = loginBean;
	}

	public UsernamePasswordAuthenticator getAuthenticator() {
		return authenticator;
	}

	public UsernamePasswordProvider getProvider() {
		return provider;
	}
}