package org.example.kickoff.view.auth;

import static javax.security.auth.message.AuthStatus.FAILURE;
import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static org.omnifaces.util.Faces.getResponse;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Faces.responseComplete;
import static org.omnifaces.util.Faces.validationFailed;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;
import static org.omnifaces.util.Messages.addGlobalError;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.security.SecurityContext;
import javax.security.auth.message.AuthStatus;
import javax.security.authentication.mechanism.http.AuthenticationParameters;
import javax.validation.constraints.NotNull;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;
import org.example.kickoff.model.validator.Password;
import org.example.kickoff.view.ActiveUser;

public abstract class AuthBacking {

	protected User user;
	protected @NotNull @Password String password;
	protected boolean rememberMe;

	@Inject
	protected UserService userService;

	@Inject
	private SecurityContext securityContext;

	@Inject
	private ActiveUser activeUser;

	@PostConstruct
	public void init() throws IOException {
		if (activeUser.isPresent()) {
			addFlashGlobalWarn("auth.message.warn.already_logged_in");
			redirect("user/profile");
		}
		else {
			user = new User();
		}
	}

	protected void authenticate(AuthenticationParameters parameters) throws IOException {
		AuthStatus status = securityContext.authenticate(getResponse(), parameters);

		if (status.equals(FAILURE)) {
			addGlobalError("auth.message.error.failure");
			validationFailed();
		}
		else if (status.equals(SEND_CONTINUE)) {
			responseComplete(); // Prevent JSF from rendering a response so authentication mechanism can continue.
        }
		else if (activeUser.getGroups().contains(Group.ADMIN)) {
			redirect("admin/users");
		}
		else if (activeUser.getGroups().contains(Group.USER)) {
			redirect("user/profile");
		}
		else {
			redirect("");
		}
	}

	public User getUser() {
		return user;
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