package org.example.kickoff.auth;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.ServletException;

import org.omnifaces.util.Faces;

@Named
@RequestScoped
public class LoginBean {

	private String loginUserName;
	private String loginPassword;
	
	public void login() {
		try {
			Faces.login(loginUserName, loginPassword);
		} catch (ServletException e) {
			e.printStackTrace();
		}
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
