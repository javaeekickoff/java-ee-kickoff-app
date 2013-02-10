package org.example.kickoff.plumbing.jaspic.dto;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationData {

	private final String userName;
	private final List<String> applicationRoles;

	public AuthenticationData(String userName, List<String> applicationRoles) {
		this.userName = userName;
		this.applicationRoles = unmodifiableList(new ArrayList<>(applicationRoles));
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getApplicationRoles() {
		return applicationRoles;
	}

}