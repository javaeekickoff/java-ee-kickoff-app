package org.example.kickoff.auth;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Authenticator implements Serializable {

	private static final long serialVersionUID = 6233826583476075733L;

	public void authenticate(String name, String password) {
		// ...
	}

	public String getUserName() {
		return "test";
	}

	public List<String> getApplicationRoles() {
		return Arrays.asList("architect");
	}

}
