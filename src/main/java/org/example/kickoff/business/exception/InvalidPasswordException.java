package org.example.kickoff.business.exception;

/**
 * Thrown when login username does exist in DB, but password does not match.
 */
public class InvalidPasswordException extends CredentialsException {

	private static final long serialVersionUID = 1L;

}