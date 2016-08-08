package org.example.kickoff.auth;
import static javax.security.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

import java.util.function.Supplier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.identitystore.CredentialValidationResult;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.credential.CallerOnlyCredential;
import javax.security.identitystore.credential.Credential;
import javax.security.identitystore.credential.UsernamePasswordCredential;

import org.example.kickoff.business.EmailNotVerifiedException;
import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;

@RequestScoped
public class KickOffIdentityStore implements IdentityStore {

	@Inject
	private UserService userService;

	@Override
	public CredentialValidationResult validate(Credential credential) {
		if (credential instanceof UsernamePasswordCredential) {
			return validate((UsernamePasswordCredential) credential);
		}

		if (credential instanceof CallerOnlyCredential) {
			return validate((CallerOnlyCredential) credential);
		}

		return NOT_VALIDATED_RESULT;
	}

	public CredentialValidationResult validate(UsernamePasswordCredential credential) {
		return validate(() -> userService
								.getUserByEmailAndPassword(
									credential.getCaller(),
									credential.getPasswordAsString()));
	}

	public CredentialValidationResult validate(CallerOnlyCredential credential) {
		return validate(() -> userService
								.getUserByEmail(
									credential.getCaller())
								.orElseThrow(InvalidCredentialsException::new));
	}

	public CredentialValidationResult validate(Supplier<User> userSupplier) {
		try {

			User user = userSupplier.get();
			if (!user.isEmailVerified()) {
				throw new EmailNotVerifiedException();
			}

			return new CredentialValidationResult(
				userSupplier.get().getEmail(),
				userSupplier.get().getRolesAsStrings());

		} catch (InvalidCredentialsException e) {
			return INVALID_RESULT;
		}
	}

}