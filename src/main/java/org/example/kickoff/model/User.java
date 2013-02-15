package org.example.kickoff.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.example.kickoff.validator.Email;

@Entity
public class User extends BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@NotNull
	@Email
	@Column(nullable = false, unique = true)
	private String email;

	@OneToOne(mappedBy = "user", fetch = LAZY, cascade = ALL)
	private Credentials credentials;

	private String loginToken; // TODO: make collection later

	@ElementCollection(targetClass = Group.class, fetch = EAGER)
	@Enumerated(STRING)
	@CollectionTable(name = "user_group")
	@Column(name = "group_name")
	private List<Group> groups = new ArrayList<>();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> getRoles() {
		Set<String> roles = new HashSet<>();

		for (Group group : getGroups()) {
			for (Role role : group.getRoles()) {
				roles.add(role.name());
			}
		}

		return new ArrayList<>(roles);
	}

}
