package org.example.kickoff.model;

import static jakarta.persistence.EnumType.STRING;
import static java.time.temporal.ChronoUnit.MONTHS;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.omnifaces.persistence.model.GeneratedIdEntity;

@Entity
public class LoginToken extends GeneratedIdEntity<Long> {

	private static final long serialVersionUID = 1L;

	private static final int HASH_LENGTH = 32;
	public static final int IP_ADDRESS_MAXLENGTH = 45;
	public static final int DESCRIPTION_MAXLENGTH = 255;

	public enum TokenType {
		REMEMBER_ME,
		API,
		RESET_PASSWORD
	}

	@Column(length = HASH_LENGTH, nullable = false, unique = true)
	private @NotNull byte[] tokenHash;

	@Column(nullable = false)
	private @NotNull Instant created;

	@Column(nullable = false)
	private @NotNull Instant expiration;

	@Column(length = IP_ADDRESS_MAXLENGTH, nullable = false)
	private @NotNull @Size(max = IP_ADDRESS_MAXLENGTH) String ipAddress;

	@Column(length = DESCRIPTION_MAXLENGTH)
	private @Size(max = DESCRIPTION_MAXLENGTH) String description;

	@ManyToOne(optional = false)
	private Person person;

	@Enumerated(STRING)
	private TokenType type;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public byte[] getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(byte[] tokenHash) {
		this.tokenHash = tokenHash;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getExpiration() {
		return expiration;
	}

	public void setExpiration(Instant expiration) {
		this.expiration = expiration;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	@PrePersist
	public void setTimestamps() {
		created = Instant.now();

		if (expiration == null) {
			expiration = created.plus(1, MONTHS);
		}
	}

}