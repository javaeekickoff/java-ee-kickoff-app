package org.example.kickoff.model;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.example.kickoff.model.validator.Email;
import org.hibernate.annotations.Cache;
import org.omnifaces.persistence.model.TimestampedEntity;

@Entity
public class User extends TimestampedEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column
	private @NotNull Instant created;

	@Column
	private @NotNull Instant lastModified;

	@Column(nullable = false, unique = true)
	private @NotNull @Email String email;

	@Column
	private @NotNull String fullName;

	/*
	 * TODO: implement.
	 */
	@Column
	private boolean emailVerified = true; // For now.

	/*
	 * The relation between User and Credentials is actually @OneToOne, but this generated in current Hibernate version
	 * a lot of spurious queries for the Credentials table, even though this relation is lazy. This hack circumvents
	 * this by using a list to force that the relation is really lazily-loaded and to prevent a large number of
	 * additional queries to the database.
	 */
	@OneToMany(mappedBy = "user", fetch = LAZY, cascade = ALL, orphanRemoval = true)
	private final Set<Credentials> credentials = new HashSet<>(1);

	@OneToMany(cascade = ALL, mappedBy = "user", orphanRemoval = true)
	@Cache(usage = TRANSACTIONAL)
	private List<LoginToken> loginTokens = new ArrayList<>();

	@Column(name = "group_name")
	@Enumerated(STRING) @ElementCollection(fetch = EAGER) @CollectionTable(name = "user_group")
	private List<Group> groups = new ArrayList<>();

	@Column
	private Instant lastLogin;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Instant getCreated() {
		return created;
	}

	@Override
	public void setCreated(Instant created) {
		this.created = created;
	}

	@Override
	public Instant getLastModified() {
		return lastModified;
	}

	@Override
	public void setLastModified(Instant lastModified) {
		this.lastModified = lastModified;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Credentials getCredentials() {
		return credentials.isEmpty() ? null : credentials.iterator().next();
	}

	public void setCredentials(Credentials credentials) {
		if (credentials == null) {
			this.credentials.clear();
		}
		else if (this.credentials.isEmpty()) {
			this.credentials.add(credentials);
		}
		else if (!this.credentials.contains(credentials)) {
			this.credentials.clear();
			this.credentials.add(credentials);
		}
	}

	public List<LoginToken> getLoginTokens() {
		return loginTokens;
	}

	public void setLoginTokens(List<LoginToken> loginTokens) {
		this.loginTokens = loginTokens;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Instant getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Instant lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Transient
	public Stream<Role> getRolesAsStream() {
		return groups.stream().flatMap(g -> g.getRoles().stream()).distinct();
	}

	@Transient
	public List<String> getRolesAsStrings() {
		return getRolesAsStream().map(Role::name).collect(toList());
	}

}