package org.example.kickoff.model;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Size;

@Entity
@Table(name = "login_token")
public class LoginToken extends BaseEntity<Long> {

	public enum TokenType {REMEMBER_ME, API, RESET_PASSWORD}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "token_hash", length = 32, unique = true)
	private byte[] tokenHash;

	@Temporal(TIMESTAMP)
	private Date created;

	@Temporal(TIMESTAMP)
	private Date expiration;

	@Column(name="ip_address")
	private String ipAddress;

	@Size(max = 256)
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private TokenType type;

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

	public byte[] getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(byte[] tokenHash) {
		this.tokenHash = tokenHash;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
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
		created = new Date();

		if (expiration == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, 1);

			expiration = calendar.getTime();
		}
	}

}