package org.example.kickoff.model.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

	@Override
	public void initialize(Email constraintAnnotation) {
		//
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (email == null) {
			return true; // Let @NotNull handle this.
		}

		try {
			new InternetAddress(email).validate();
		}
		catch (AddressException e) {
			return false;
		}

		return true;
	}

}