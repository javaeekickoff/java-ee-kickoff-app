package org.example.kickoff.view.auth;

import static javax.security.auth.message.AuthStatus.FAILURE;
import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static javax.security.authentication.mechanism.http.AuthenticationParameters.withParams;
import static org.omnifaces.util.Faces.getResponse;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Faces.responseComplete;
import static org.omnifaces.util.Faces.validationFailed;
import static org.omnifaces.util.Messages.addFlashGlobalInfo;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;
import static org.omnifaces.util.Messages.addGlobalError;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.SecurityContext;
import javax.security.auth.message.AuthStatus;
import javax.security.identitystore.credential.CallerOnlyCredential;
import javax.validation.constraints.NotNull;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.example.kickoff.model.validator.Password;
import org.example.kickoff.view.ActiveUser;

@Named
@RequestScoped
public class SignupBacking {

	private User user;
	private @NotNull @Password String password;

	@Inject
	private ActiveUser activeUser;

	@Inject
	private SecurityContext securityContext;

	@Inject
	private UserService userService;

	@PostConstruct
	public void init() throws IOException {
		if (activeUser.isPresent()) {
			addFlashGlobalWarn("login.message.info.already_logged_in");
			redirect("user/profile");
		}
		else {
			user = new User();
		}
	}

	public void signup() throws IOException {
		userService.registerUser(user, password);

		AuthStatus status = securityContext.authenticate(getResponse(),
        	withParams()
        		.credential(new CallerOnlyCredential(user.getEmail())));

        if (status.equals(SEND_CONTINUE)) {
			responseComplete(); // Prevent JSF from rendering a response so authentication mechanism can continue.
        }
		else if (status.equals(FAILURE)) {
			addGlobalError("signup.message.error.authentication_failure");
			validationFailed();
		}
		else {
			addFlashGlobalInfo("signup.message.info.signed_up");
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

}