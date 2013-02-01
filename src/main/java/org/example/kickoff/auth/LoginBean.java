package org.example.kickoff.auth;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.ServletException;

import org.omnifaces.util.Faces;

@Named
@RequestScoped
public class LoginBean {

	private String loginUserName;
	private String loginPassword;

	public void login() throws IOException, ServletException {
		// Doesn't seem to be supported in JASPIC
		// Faces.login(loginUserName, loginPassword);

		Faces.getRequest().authenticate(Faces.getResponse());
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
