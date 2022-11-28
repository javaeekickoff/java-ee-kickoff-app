package org.example.kickoff.config.auth;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import static org.example.kickoff.model.Group.USER;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.CallerOnlyCredential;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import org.example.kickoff.business.exception.EmailNotVerifiedException;
import org.example.kickoff.business.exception.InvalidGroupException;
import org.example.kickoff.business.exception.CredentialsException;
import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@ApplicationScoped
public class KickoffIdentityStore implements IdentityStore {

	@Inject
	private UserService userService;

	@Override
	public CredentialValidationResult validate(Credential credential) {
		Supplier<User> userSupplier = null;

		if (credential instanceof UsernamePasswordCredential) {
			String email = ((UsernamePasswordCredential) credential).getCaller();
			String password = ((UsernamePasswordCredential) credential).getPasswordAsString();
			userSupplier = () -> userService.getByEmailAndPassword(email, password);
		}
		else if (credential instanceof CallerOnlyCredential) {
			String email = ((CallerOnlyCredential) credential).getCaller();
			userSupplier = () -> userService.getByEmail(email);
		}

		return validate(userSupplier);
	}

	static CredentialValidationResult validate(Supplier<User> userSupplier) {
		if (userSupplier == null) {
			return NOT_VALIDATED_RESULT;
		}

		try {
			User user = userSupplier.get();

			if (!user.getGroups().contains(USER)) {
				throw new InvalidGroupException();
			}

			if (!user.isEmailVerified()) {
				throw new EmailNotVerifiedException();
			}

			return new CredentialValidationResult(new KickoffCallerPrincipal(user), user.getRolesAsStrings());
		}
		catch (CredentialsException e) {
			return INVALID_RESULT;
		}
	}

}