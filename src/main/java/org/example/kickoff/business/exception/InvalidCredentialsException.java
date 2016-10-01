package org.example.kickoff.business.exception;

/**
 * Thrown when login username + password combination does not match,
 * regardless of whether the login username does exist or not.
 */
public class InvalidCredentialsException extends BusinessException {

	private static final long serialVersionUID = 1L;

}