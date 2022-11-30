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

import org.example.kickoff.business.exception.CredentialsException;
import org.example.kickoff.business.exception.EmailNotVerifiedException;
import org.example.kickoff.business.exception.InvalidGroupException;
import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;

@ApplicationScoped
public class KickoffIdentityStore implements IdentityStore {

	@Inject
	private PersonService personService;

	@Override
	public CredentialValidationResult validate(Credential credential) {
		Supplier<Person> personSupplier = null;

		if (credential instanceof UsernamePasswordCredential) {
			String email = ((UsernamePasswordCredential) credential).getCaller();
			String password = ((UsernamePasswordCredential) credential).getPasswordAsString();
			personSupplier = () -> personService.getByEmailAndPassword(email, password);
		}
		else if (credential instanceof CallerOnlyCredential) {
			String email = ((CallerOnlyCredential) credential).getCaller();
			personSupplier = () -> personService.getByEmail(email);
		}

		return validate(personSupplier);
	}

	static CredentialValidationResult validate(Supplier<Person> personSupplier) {
		if (personSupplier == null) {
			return NOT_VALIDATED_RESULT;
		}

		try {
			Person person = personSupplier.get();

			if (!person.getGroups().contains(USER)) {
				throw new InvalidGroupException();
			}

			if (!person.isEmailVerified()) {
				throw new EmailNotVerifiedException();
			}

			return new CredentialValidationResult(new KickoffCallerPrincipal(person), person.getRolesAsStrings());
		}
		catch (CredentialsException e) {
			return INVALID_RESULT;
		}
	}

}