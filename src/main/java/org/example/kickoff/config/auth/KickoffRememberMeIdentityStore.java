package org.example.kickoff.config.auth;

import static org.example.kickoff.model.LoginToken.TokenType.REMEMBER_ME;
import static org.omnifaces.util.Servlets.getRemoteAddr;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.http.HttpServletRequest;

import org.example.kickoff.business.service.LoginTokenService;
import org.example.kickoff.business.service.UserService;

@ApplicationScoped
public class KickoffRememberMeIdentityStore implements RememberMeIdentityStore {

	@Inject
	private HttpServletRequest request;

	@Inject
	private UserService userService;

	@Inject
	private LoginTokenService loginTokenService;

	@Override
	public CredentialValidationResult validate(RememberMeCredential credential) {
		return KickoffIdentityStore.validate(() -> userService.getByLoginToken(credential.getToken(), REMEMBER_ME));
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