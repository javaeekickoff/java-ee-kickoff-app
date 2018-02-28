package org.example.kickoff.business.exception;

/**
 * Thrown when login token does not exist in DB or is expired.
 */
public class InvalidTokenException extends CredentialsException {

	private static final long serialVersionUID = 1L;

}