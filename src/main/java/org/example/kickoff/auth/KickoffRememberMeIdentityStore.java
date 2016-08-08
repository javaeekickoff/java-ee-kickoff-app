package org.example.kickoff.auth;

import static java.util.logging.Level.WARNING;
import static javax.security.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.identitystore.CredentialValidationResult.Status.VALID;
import static org.example.kickoff.model.LoginToken.TokenType.REMEMBER_ME;
import static org.omnifaces.util.Utils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.CallerPrincipal;
import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.RememberMeIdentityStore;
import javax.security.identitystore.credential.RememberMeCredential;
import javax.servlet.http.HttpServletRequest;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;


@ApplicationScoped
public class KickoffRememberMeIdentityStore implements RememberMeIdentityStore {

	private static final Logger logger = Logger.getLogger(KickoffRememberMeIdentityStore.class.getName());

	@Inject
	private HttpServletRequest httpServletRequest;

	@Inject
	private UserService userService;

	@Override
	public CredentialValidationResult validate(RememberMeCredential credential) {
		try {
			Optional<User> optionalUser = userService.getUserByLoginToken(credential.getToken(), REMEMBER_ME);

			if (optionalUser.isPresent()) {
				User user = optionalUser.get();

				return new CredentialValidationResult(VALID, new CallerPrincipal(user.getEmail()), user.getRolesAsStrings());
			}
		}
		catch (Exception e) {
			logger.log(WARNING, "Unable to authenticate user using token.", e);
		}

		return INVALID_RESULT;
	}

	@Override
	public String generateLoginToken(CallerPrincipal callerPrincipal, List<String> groups) {
		return userService.generateLoginToken(callerPrincipal.getName(), getRemoteAddr(), getDescription(), REMEMBER_ME);
	}

	@Override
	public void removeLoginToken(String loginToken) {
		if (loginToken != null) {
			userService.removeLoginToken(loginToken);
		}
	}

	private String getRemoteAddr() {
		String forwardedFor = httpServletRequest.getHeader("X-Forwarded-For");

		if (!isEmpty(forwardedFor)) {
			return forwardedFor.split("\\s*,\\s*", 2)[0]; // It's a comma separated string: client,proxy1,proxy2,...
		}

		return httpServletRequest.getRemoteAddr();
	}

	private String getDescription() {
		return "Remember me session: " + httpServletRequest.getHeader("User-Agent");
	}

}