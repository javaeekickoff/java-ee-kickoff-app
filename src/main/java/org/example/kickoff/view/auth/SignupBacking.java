package org.example.kickoff.view.auth;

import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.credential.CallerOnlyCredential;

@Named
@RequestScoped
public class SignupBacking extends AuthBacking {

	public void signup() throws IOException {
		personService.register(person, password);
		authenticate(withParams().credential(new CallerOnlyCredential(person.getEmail())));
	}

}