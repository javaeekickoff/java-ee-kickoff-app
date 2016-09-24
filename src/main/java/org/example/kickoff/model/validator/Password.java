package org.example.kickoff.model.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = PasswordValidator.class)
@Documented
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Password {

	String message() default "{invalid.password}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}