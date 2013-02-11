package org.example.kickoff.auth;

import static java.util.Arrays.asList;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordAuthenticator;

@SessionScoped
public class Authenticator implements UsernamePasswordAuthenticator {

	private static final long serialVersionUID = 6233826583476075733L;

	@EJB
	private UserService userService;

	private User user;
	private List<String> applicationRoles;

	public boolean authenticate(String name, String password) {
		try {
			user = userService.getUserByCredentials(name, password);
		}
		catch (InvalidCredentialsException e) {
			return false;
		}
		applicationRoles = asList("architect");

		return true;
	}

	public String getUserName() {
		return user == null ? null : user.getEmail();
	}

	public List<String> getApplicationRoles() {
		return applicationRoles;
	}

}
