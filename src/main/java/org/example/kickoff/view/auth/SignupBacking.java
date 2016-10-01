package org.example.kickoff.view.auth;

import static javax.security.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.security.identitystore.credential.CallerOnlyCredential;

@Named
@RequestScoped
public class SignupBacking extends AuthBacking {

	public void signup() throws IOException {
		userService.registerUser(user, password);
		authenticate(withParams().credential(new CallerOnlyCredential(user.getEmail())));
	}

}