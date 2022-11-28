package org.example.kickoff.model;

import static org.omnifaces.utils.security.MessageDigests.digest;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.omnifaces.persistence.model.GeneratedIdEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Credentials extends GeneratedIdEntity<Long> {

	private static final long serialVersionUID = 1L;

	private static final int HASH_LENGTH = 32;
	private static final int SALT_LENGTH = 40;

	@ManyToOne(optional = false)
	private @NotNull User user;

	@Column(length = HASH_LENGTH, nullable = false)
	private @NotNull byte[] passwordHash;

	@Column(length = SALT_LENGTH, nullable = false)
	private @NotNull byte[] salt = new byte[SALT_LENGTH];

	public void setUser(User user) {
		user.setCredentials(this);
		this.user = user;
	}

	public void setPassword(String password) {
		ThreadLocalRandom.current().nextBytes(salt);
		passwordHash = hash(password);
	}

	public boolean isValid(String password) {
		return Arrays.equals(passwordHash, hash(password));
	}

	private byte[] hash(String password) {
		return digest(password, salt, "SHA-256");
	}

}