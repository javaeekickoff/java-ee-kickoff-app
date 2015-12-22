package org.example.kickoff.view.general;

import static org.omnifaces.util.Messages.addGlobalError;
import static org.omnifaces.util.Messages.addGlobalInfo;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.Size;

import org.example.kickoff.business.UserService;
import org.example.kickoff.business.ValidationException;
import org.example.kickoff.model.User;
import org.omnifaces.security.jaspic.core.AuthParameters;
import org.omnifaces.security.jaspic.core.Jaspic;

@Named
@RequestScoped
public class RegisterBean {

	@EJB
	private UserService userService;

	private User user = new User();

	@Size(min = 8)
	private String password;
	
	private boolean rememberMe;
	
	public void register() {
		try {
			userService.registerUser(user, password);
		}
		catch (ValidationException e) {
			addGlobalError(e.getMessage());
		}
		
		boolean authenticated = Jaspic.authenticate(
	        new AuthParameters()
                .username(user.getEmail())
                .password(password)
                .rememberMe(rememberMe)
        );

		if (!authenticated) {
			addGlobalError("Login failed");
		} else {
			addGlobalInfo("User {0} successfully registered" , user.getEmail());
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}