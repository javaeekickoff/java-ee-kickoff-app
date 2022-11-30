package org.example.kickoff.view.validator;

import static org.omnifaces.util.Messages.createError;

import java.util.Optional;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;

@FacesValidator("emailVerifiedValidator")
public class EmailVerifiedValidator implements Validator<String> {

	@Inject
	private PersonService personService;

	@Override
	public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
		if (value == null) {
			return;
		}

		Optional<Person> optionalPerson = personService.findByEmail(value);

		if (optionalPerson.isPresent() && !optionalPerson.get().isEmailVerified()) {
			throw new ValidatorException(createError("emailVerifiedValidator"));
		}
	}

}