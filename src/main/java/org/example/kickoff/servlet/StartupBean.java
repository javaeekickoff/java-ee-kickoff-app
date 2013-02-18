package org.example.kickoff.servlet;

import static org.example.kickoff.model.Group.ADMINISTRATORS;
import static org.example.kickoff.model.Group.USERS;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;

@ApplicationScoped
public class StartupBean {
	
	@EJB
	private UserService userService;
	
	@PostConstruct
	void init() {
	 	try {
			userService.getUserByCredentials("foo@bar.com", "foo");
		} catch (InvalidCredentialsException e) {
			User user = new User();
			user.setEmail("foo@bar.com");
			
			List<Group> group = new ArrayList<>();
			group.add(ADMINISTRATORS);
			group.add(USERS);

			user.setGroups(group);
			
			userService.registerUser(user, "foo");
		}
    	
		try {
			userService.getUserByCredentials("foo1@bar.com", "foo");
		} catch (InvalidCredentialsException e) {
			User user = new User();
			user.setEmail("foo1@bar.com");
			
			List<Group> group = new ArrayList<>();
			group.add(ADMINISTRATORS);
			group.add(USERS);

			user.setGroups(group);
			
			userService.registerUser(user, "foo");
		}
	}
	
	public void noop() {}

}
