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
import javax.security.identitystore.credential.UsernamePasswordCredential;
import javax.validation.constraints.NotNull;

import org.example.kickoff.model.validator.Email;
import org.example.kickoff.model.validator.Password;
import org.example.kickoff.view.ActiveUser;
import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class LoginBacking {

	private @NotNull @Email String email;
	private @NotNull @Password String password;
	private boolean rememberMe;

	@Inject @Param(name = "continue") // Defined in @LoginToContinue of KickoffFormAuthenticationMechanism.
	private boolean loginToContinue;

	@Inject
	private ActiveUser activeUser;

	@Inject
	private SecurityContext securityContext;

	@PostConstruct
	public void init() throws IOException {
		if (activeUser.isPresent()) {
			addFlashGlobalWarn("login.message.info.already_logged_in");
			redirect("user/profile");
		}
	}

	public void login() throws IOException {
		AuthStatus status =	securityContext.authenticate(getResponse(),
			withParams()
				.credential(new UsernamePasswordCredential(email, password))
				.newAuthentication(!loginToContinue)
				.rememberMe(rememberMe));

		if (status.equals(SEND_CONTINUE)) {
			responseComplete(); // Prevent JSF from rendering a response so authentication mechanism can continue.
		}
		else if (status.equals(FAILURE)) {
			addGlobalError("login.message.error.authentication_failure");
			validationFailed();
		}
		else {
			addFlashGlobalInfo("login.message.info.logged_in");
			redirect("");
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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