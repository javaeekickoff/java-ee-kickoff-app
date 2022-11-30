package org.example.kickoff.config.auth;

import static org.example.kickoff.model.LoginToken.TokenType.REMEMBER_ME;
import static org.omnifaces.util.Servlets.getRemoteAddr;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.RememberMeIdentityStore;
import jakarta.servlet.http.HttpServletRequest;

import org.example.kickoff.business.service.LoginTokenService;
import org.example.kickoff.business.service.PersonService;

@ApplicationScoped
public class KickoffRememberMeIdentityStore implements RememberMeIdentityStore {

	@Inject
	private HttpServletRequest request;

	@Inject
	private PersonService personService;

	@Inject
	private LoginTokenService loginTokenService;

	@Override
	public CredentialValidationResult validate(RememberMeCredential credential) {
		return KickoffIdentityStore.validate(() -> personService.getByLoginToken(credential.getToken(), REMEMBER_ME));
	}

	@Override
	public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
		String ipAddress = getRemoteAddr(request);
		String description = "Remember me session for " + ipAddress + " on " + request.getHeader("User-Agent");
		return loginTokenService.generate(callerPrincipal.getName(), ipAddress, description, REMEMBER_ME);
	}

	@Override
	public void removeLoginToken(String loginToken) {
		loginTokenService.remove(loginToken);
	}

}