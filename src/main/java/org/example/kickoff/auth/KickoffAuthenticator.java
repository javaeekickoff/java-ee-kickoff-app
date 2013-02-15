package org.example.kickoff.auth;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;
import org.example.kickoff.plumbing.jaspic.user.TokenAuthenticator;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordAuthenticator;

@SessionScoped
public class KickoffAuthenticator implements UsernamePasswordAuthenticator, TokenAuthenticator {

	private static final long serialVersionUID = 6233826583476075733L;

	@EJB
	private UserService userService;

	private User user;
	private List<String> applicationRoles;

	@Override
	public boolean authenticate(String name, String password) {
		try {
			user = userService.getUserByCredentials(name, password);
		}
		catch (InvalidCredentialsException e) {
			return false;
		}
		applicationRoles = user.getRoles();

		return true;
	}

	@Override
	public boolean authenticate(String loginToken) {
		try {
			user = userService.getUserByLoginToken(loginToken);
		}
		catch (InvalidCredentialsException e) {
			return false;
		}
		applicationRoles = user.getRoles();

		return true;
	}

	@Override
	public String generateLoginToken() {
		return userService.generateLoginToken(user.getEmail());
	}
	
	@Override
	public void removeLoginToken() {
		userService.removeLoginToken(user.getEmail());
	}	

	@Override
	public String getUserName() {
		return user == null ? null : user.getEmail();
	}

	@Override
	public List<String> getApplicationRoles() {
		return applicationRoles;
	}

}
