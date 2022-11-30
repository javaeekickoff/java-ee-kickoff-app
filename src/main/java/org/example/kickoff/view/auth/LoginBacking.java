package org.example.kickoff.view.auth;

import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;

import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class LoginBacking extends AuthBacking {

	@Inject @Param(name = "continue") // Defined in @LoginToContinue of KickoffFormAuthenticationMechanism.
	private boolean loginToContinue;

	public void login() throws IOException {
		authenticate(withParams()
			.credential(new UsernamePasswordCredential(person.getEmail(), password))
			.newAuthentication(!loginToContinue)
			.rememberMe(rememberMe));
	}

}