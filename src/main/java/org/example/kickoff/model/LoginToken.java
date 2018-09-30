package org.example.kickoff.model;

import static java.time.temporal.ChronoUnit.MONTHS;
import static javax.persistence.EnumType.STRING;
import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
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
	@Cache(usage = TRANSACTIONAL)
	private User user;

	@Enumerated(STRING)
	private TokenType type;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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