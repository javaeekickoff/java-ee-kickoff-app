package org.example.kickoff.model.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

@Constraint(validatedBy = EmailValidator.class)
@Size(max = 254, message = "{invalid.email}")
@Documented
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Email {

	String message() default "{invalid.email}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}