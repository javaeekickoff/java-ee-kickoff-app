package org.example.kickoff.model.validator;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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