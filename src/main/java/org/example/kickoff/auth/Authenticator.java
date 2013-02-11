package org.example.kickoff.auth;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Authenticator implements Serializable {

	private static final long serialVersionUID = 6233826583476075733L;
	
	private String userName;
	private List<String> applicationRoles;

	public boolean authenticate(String name, String password) {
		userName = name;
		applicationRoles = asList("architect");
		
		return true;
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getApplicationRoles() {
		return applicationRoles;
	}

}
