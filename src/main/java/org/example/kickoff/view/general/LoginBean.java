package org.example.kickoff.view.general;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.ServletException;

import org.omnifaces.security.jaspic.Jaspic;
import org.omnifaces.util.Messages;

@Named
@RequestScoped
public class LoginBean {

	private String loginUserName;
	private String loginPassword;
	private boolean rememberMe;

	public void login() throws IOException, ServletException {

		// Not supported in JASPIC: (only WebSphere calls the SAM, but with WebSphere specific entries in MessageInfo Map)
		// Faces.login(loginUserName, loginPassword);

		boolean authenticated = Jaspic.authenticate(loginUserName, loginPassword, rememberMe);

		if (!authenticated) {
			Messages.addGlobalError("Login failed");
		}
	}

	public void logout() throws ServletException {
		Jaspic.logout();
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
