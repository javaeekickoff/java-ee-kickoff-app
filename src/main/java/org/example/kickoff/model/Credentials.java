package org.example.kickoff.model;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="mycredentials")
public class Credentials extends BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@OneToOne(fetch = EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "password_hash", length = 32)
	private byte[] passwordHash;

	@NotNull
	@Column(length = 40)
	private byte[] salt;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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
