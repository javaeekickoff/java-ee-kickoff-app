package org.example.kickoff.auth;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.ServletException;

import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordProvider;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

@Named
@RequestScoped
public class LoginBean implements UsernamePasswordProvider {

	private String loginUserName;
	private String loginPassword;

	public void login() throws IOException, ServletException {

		// Not supported in JASPIC: (only WebSphere calls the SAM, but with WebSphere specific entries in MessageInfo Map)
		// Faces.login(loginUserName, loginPassword);

		boolean authenticated = Faces.getRequest().authenticate(Faces.getResponse());

		if (!authenticated) {
			Messages.addGlobalError("Login failed");
		}
	}

	public void logout() throws ServletException {
		Faces.logout();
		Faces.invalidateSession();
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

}
