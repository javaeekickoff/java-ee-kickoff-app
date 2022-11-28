package org.example.kickoff.view.validator;

import static org.omnifaces.util.Messages.createError;

import java.util.Optional;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@FacesValidator("emailVerifiedValidator")
public class EmailVerifiedValidator implements Validator {

	@Inject
	private UserService userService;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		Optional<User> user = userService.findByEmail((String) value);

		if (user.isPresent() && !user.get().isEmailVerified()) {
			throw new ValidatorException(createError("emailVerifiedValidator"));
		}
	}

}