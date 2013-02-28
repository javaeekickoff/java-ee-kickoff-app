package org.example.kickoff.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

	@Override
	public void initialize(Email constraintAnnotation) {
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		try {
			new InternetAddress(email).validate();
		}
		catch (AddressException e) {
			return false;
		}
		return true;
	}

}
