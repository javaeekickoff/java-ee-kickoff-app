package org.example.kickoff.business;

import javax.ejb.ApplicationException;

@ApplicationException
public class NonDeletableEntityException extends RuntimeException{

	private static final long serialVersionUID = -5195438032391747041L;

	public NonDeletableEntityException() {
		super();
	}

	public NonDeletableEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NonDeletableEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public NonDeletableEntityException(String message) {
		super(message);
	}

	public NonDeletableEntityException(Throwable cause) {
		super(cause);
	}


}
