package org.example.kickoff.model;

import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;
import static org.omnifaces.utils.security.MessageDigests.digest;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.omnifaces.persistence.model.GeneratedIdEntity;

@Entity
public class Credentials extends GeneratedIdEntity<Long> {

	private static final long serialVersionUID = 1L;

	private static final int HASH_LENGTH = 32;
	private static final int SALT_LENGTH = 40;

	@ManyToOne(optional = false)
	@Cache(usage = TRANSACTIONAL)
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