package org.example.kickoff.view.general;

import static javax.security.auth.message.AuthStatus.FAILURE;
import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static javax.security.authentication.mechanism.http.AuthenticationParameters.withParams;
import static org.omnifaces.util.Faces.getRequestBaseURL;
import static org.omnifaces.util.Faces.getResponse;
import static org.omnifaces.util.Faces.invalidateSession;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Faces.responseComplete;
import static org.omnifaces.util.Messages.addFlashGlobalInfo;
import static org.omnifaces.util.Messages.addGlobalError;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.SecurityContext;
import javax.security.auth.message.AuthStatus;
import javax.security.identitystore.credential.Credential;
import javax.security.identitystore.credential.UsernamePasswordCredential;
import javax.servlet.ServletException;

import org.omnifaces.cdi.Param;
import org.omnifaces.util.Faces;


@Named
@RequestScoped
public class LoginBean {

	private String loginUserName;
	private String loginPassword;
	private boolean rememberMe;
	
	@Inject
    @Param(name = "cnt")
    private boolean loginToContinue;
	
    @Inject
    private SecurityContext securityContext;
	
	public void loginNew() throws IOException, ServletException {
	    loginToContinue = false;
		login();
		redirect(getRequestBaseURL()); // Would use navigation outcome, but we need to redirect to / here.
	}

	public void login() throws IOException, ServletException {
		
        Credential credential = new UsernamePasswordCredential(loginUserName, loginPassword);
        
        AuthStatus status = securityContext.authenticate(
            getResponse(), 
            withParams()
                .credential(credential)
                .newAuthentication(!loginToContinue));
        
        if (status.equals(SEND_CONTINUE)) {
            // Authentication mechanism has send a redirect, should not
            // send anything to response from JSF now.
            responseComplete();
        } else if (status.equals(FAILURE)) {
        	addGlobalError("Authentication failed");
        }
	}

	public void logout() throws ServletException, IOException {
		Faces.logout();
		invalidateSession();
		addFlashGlobalInfo("You have been logged-out.");
		redirect(getRequestBaseURL()); // Would use navigation outcome, but we need to redirect to / here.
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
