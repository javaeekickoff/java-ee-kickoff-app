package org.example.kickoff.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

	@Override
	public void initialize(Email arg0) {
	}

	@Override
	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		try {
			new InternetAddress(arg0).validate();
		}
		catch (AddressException e) {
			return false;
		}
		return true;
	}

}
