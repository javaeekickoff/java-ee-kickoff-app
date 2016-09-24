package org.example.kickoff.config.auth;
import static javax.security.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.credential.CallerOnlyCredential;
import javax.security.identitystore.credential.Credential;
import javax.security.identitystore.credential.UsernamePasswordCredential;

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
				credential.getCaller(), ((UsernamePasswordCredential) credential).getPasswordAsString()));
		}

		if (credential instanceof CallerOnlyCredential) {
			return validate(() -> userService.getByEmail(
				credential.getCaller()).orElseThrow(InvalidCredentialsException::new));
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