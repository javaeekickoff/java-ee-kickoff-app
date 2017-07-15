package org.example.kickoff.config.auth;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import org.example.kickoff.business.exception.EmailNotVerifiedException;
import org.example.kickoff.business.exception.InvalidCredentialsException;
import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@ApplicationScoped
public class KickoffIdentityStore implements IdentityStore {

	@Inject
	private UserService userService;

	@Override
	public CredentialValidationResult validate(Credential credential) {
		if (credential instanceof UsernamePasswordCredential) {
			return validate(() -> userService.getByEmailAndPassword(
				((UsernamePasswordCredential) credential).getCaller(), ((UsernamePasswordCredential) credential).getPasswordAsString()));
		}

		if (credential instanceof CallerOnlyCredential) {
			return validate(() -> userService.getByEmail(
				((CallerOnlyCredential) credential).getCaller()).orElseThrow(InvalidCredentialsException::new));
		}

		return NOT_VALIDATED_RESULT;
	}

	private CredentialValidationResult validate(Supplier<User> userSupplier) {
		try {
			User user = userSupplier.get();
			if (!user.isEmailVerified()) {
				throw new EmailNotVerifiedException();
			}

			return new CredentialValidationResult(user.getEmail(), user.getRolesAsStrings());
		}
		catch (InvalidCredentialsException e) {
			return INVALID_RESULT;
		}
	}

}