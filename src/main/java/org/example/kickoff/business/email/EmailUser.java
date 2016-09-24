package org.example.kickoff.business.email;

import javax.mail.internet.InternetAddress;

import org.example.kickoff.model.User;

public class EmailUser {

	private final Long id;
	private final String email;
	private final String fullName;

	public EmailUser(User user) {
		this(user.getId(), user.getEmail(), user.getFullName());
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