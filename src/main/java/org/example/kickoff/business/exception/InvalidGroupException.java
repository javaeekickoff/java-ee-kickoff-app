package org.example.kickoff.business.exception;

/**
 * Thrown when login username does exist in DB, but user does not have the correct group.
 */
public class InvalidGroupException extends CredentialsException {

	private static final long serialVersionUID = 1L;

}