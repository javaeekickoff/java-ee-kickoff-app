package org.example.kickoff.view.general;

import static javax.security.auth.message.AuthStatus.FAILURE;
import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static javax.security.authentication.mechanism.http.AuthenticationParameters.withParams;
import static org.omnifaces.util.Faces.getResponse;
import static org.omnifaces.util.Messages.addGlobalError;
import static org.omnifaces.util.Messages.addGlobalInfo;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.SecurityContext;
import javax.security.auth.message.AuthStatus;
import javax.security.identitystore.credential.CallerOnlyCredential;
import javax.security.identitystore.credential.Credential;
import javax.validation.constraints.Size;

import org.example.kickoff.business.UserService;
import org.example.kickoff.business.ValidationException;
import org.example.kickoff.model.User;
import org.omnifaces.util.Faces;

@Named
@RequestScoped
public class RegisterBean {

	@Inject
	private SecurityContext securityContext;

	@Inject
	private UserService userService;

	private User user = new User();

	@Size(min = 8)
	private String password;

	private boolean rememberMe;

	public void register() {
		try {
			userService.registerUser(user, password);
		} catch (ValidationException e) {
			addGlobalError(e.getMessage());
		}
		
		Credential credential = new CallerOnlyCredential(user.getEmail());
        
        AuthStatus status = securityContext.authenticate(
            getResponse(), 
            withParams()
                .credential(credential));
        
        if (status.equals(SEND_CONTINUE)) {
            // Authentication mechanism has send a redirect, should not
            // send anything to response from JSF now.
            Faces.responseComplete();
        } else if (status.equals(FAILURE)) {
        	addGlobalError("Authentication failed");
        	return;
        }
		
        addGlobalInfo("User {0} successfully registered", user.getEmail());
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