package org.example.kickoff.plumbing.jaspic.dto;

import org.example.kickoff.auth.Authenticator;
import org.example.kickoff.auth.LoginBean;

public class Delegators {

	private final Authenticator authenticator;
	private final LoginBean loginBean;

	public Delegators(Authenticator authenticator, LoginBean loginBean) {
		this.authenticator = authenticator;
		this.loginBean = loginBean;
	}

	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}
}