package org.example.kickoff.model;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.example.kickoff.model.Role.TEST_SERVLET_ACCESS;
import static org.example.kickoff.model.Role.USER_MANAGEMENT;

import java.util.List;

public enum Group {

	USERS, ADMINISTRATORS(TEST_SERVLET_ACCESS, USER_MANAGEMENT);

	private List<Role> roles;

	private Group(Role... roles) {
		this.roles = unmodifiableList(asList(roles));
	}

	public List<Role> getRoles() {
		return roles;
	}

}
