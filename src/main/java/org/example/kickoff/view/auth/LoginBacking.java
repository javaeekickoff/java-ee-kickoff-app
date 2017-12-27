package org.example.kickoff.view.auth;

import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.credential.UsernamePasswordCredential;

import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class LoginBacking extends AuthBacking {

	@Inject @Param(name = "continue") // Defined in @LoginToContinue of KickoffFormAuthenticationMechanism.
	private boolean loginToContinue;

	public void login() {
		authenticate(withParams()
			.credential(new UsernamePasswordCredential(user.getEmail(), password))
			.newAuthentication(!loginToContinue)
			.rememberMe(rememberMe));
	}

}