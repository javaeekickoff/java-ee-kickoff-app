package org.example.kickoff.business.email;

import jakarta.mail.internet.InternetAddress;

import org.example.kickoff.model.Person;

public class EmailUser {

	private final Long id;
	private final String email;
	private final String fullName;

	public EmailUser(Person person) {
		this(person.getId(), person.getEmail(), person.getFullName());
	}

	public EmailUser(String email, String fullName) {
		this(null, email, fullName);
	}

	private EmailUser(Long id, String email, String fullName) {
		try {
			new InternetAddress(email).validate();
		}
		catch (Exception e) {
			throw new IllegalArgumentException("invalid email");
		}

		this.id = id;
		this.email = email;
		this.fullName = fullName;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

}