package org.example.kickoff.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	@Override
	public void initialize(Password constraintAnnotation) {
		//
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null) {
			return true; // Let @NotNull handle this.
		}

		return password.length() >= 8
			&& password.chars()
				.filter(c -> !isLatinLetter(c))
				.findFirst()
				.isPresent();
	}

	private static boolean isLatinLetter(int c) {
	    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

}