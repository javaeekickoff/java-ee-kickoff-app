package org.example.kickoff.view.validator;

import static org.omnifaces.util.Messages.createError;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@FacesValidator(value="emailVerifiedValidator", managed=true)
public class EmailVerifiedValidator implements Validator<String> {

	@Inject
	private UserService userService;

	@Override
	public void validate(FacesContext context, UIComponent component, String email) throws ValidatorException {
		if (email == null) {
			return;
		}

		Optional<User> user = userService.findByEmail(email);

		if (user.isPresent() && !user.get().isEmailVerified()) {
			throw new ValidatorException(createError("emailVerifiedValidator"));
		}
	}

}