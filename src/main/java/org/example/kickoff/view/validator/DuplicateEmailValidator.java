package org.example.kickoff.view.validator;

import static org.omnifaces.util.Messages.createError;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.validator.ValueChangeValidator;

@FacesValidator("duplicateEmailValidator")
public class DuplicateEmailValidator extends ValueChangeValidator {

	@Inject
	private UserService userService;

	@Override
	public void validateChangedObject(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		Optional<User> user = userService.findByEmail((String) value);

		if (user.isPresent()) {
			throw new ValidatorException(createError("duplicateEmailValidator"));
		}
	}

}