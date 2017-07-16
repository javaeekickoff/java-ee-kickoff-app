package org.example.kickoff.model;

import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.omnifaces.persistence.model.BaseEntity;

@Entity
public class Credentials extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@Cache(usage = TRANSACTIONAL)
	private User user;

	@Column(length = 32)
	private @NotNull byte[] passwordHash;

	@Column(length = 40)
	private @NotNull byte[] salt;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

}