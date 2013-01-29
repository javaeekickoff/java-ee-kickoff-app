package org.example.kickoff.business;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -5207093849770922249L;

	public InvalidCredentialsException() {
		super();
	}

	public InvalidCredentialsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}

}
