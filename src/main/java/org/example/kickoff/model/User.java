package org.example.kickoff.model;

import static java.util.stream.Collectors.toSet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.example.kickoff.model.validator.Email;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Formula;
import org.omnifaces.persistence.model.TimestampedEntity;

@Entity
public class User extends TimestampedEntity<Long> {

	private static final long serialVersionUID = 1L;

	public static final int EMAIL_MAXLENGTH = 254;
	public static final int NAME_MAXLENGTH = 32;

	@Column(length = EMAIL_MAXLENGTH, nullable = false, unique = true)
	private @NotNull @Size(max = EMAIL_MAXLENGTH) @Email String email;

	@Column(length = NAME_MAXLENGTH, nullable = false)
	private @NotNull @Size(max = NAME_MAXLENGTH) String firstName;

	@Column(length = NAME_MAXLENGTH, nullable = false)
	private @NotNull @Size(max = NAME_MAXLENGTH) String lastName;

	@Formula("CONCAT(firstName, ' ', lastName)")
	private String fullName;

	/*
	 * TODO: implement.
	 */
	@Column(nullable = false)
	private boolean emailVerified = true; // For now.

	@OneToOne(mappedBy = "user", fetch = LAZY, cascade = ALL)
	private Credentials credentials;

	@OneToMany(mappedBy = "user", fetch = LAZY, cascade = ALL, orphanRemoval = true)
	@Cache(usage = TRANSACTIONAL)
	private List<LoginToken> loginTokens = new ArrayList<>();

	@ElementCollection(fetch = EAGER)
	private @Enumerated(STRING) Set<Group> groups = new HashSet<>();

	@Column
	private Instant lastLogin;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public List<LoginToken> getLoginTokens() {
		return loginTokens;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Instant getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Instant lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Transient
	public Set<Role> getRoles() {
		return groups.stream().flatMap(g -> g.getRoles().stream()).collect(toSet());
	}

	@Transient
	public Set<String> getRolesAsStrings() {
		return getRoles().stream().map(Role::name).collect(toSet());
	}

}