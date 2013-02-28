package org.example.kickoff.view.admin.users;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.Size;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;

@ManagedBean
@ViewScoped
public class ChangePasswordBacking {

	@EJB
	private UserService userService;

	private User user;

	@Size(min=8)
	private String newPassword;
	
	public void changePassword() {
		userService.updatePassword(user, newPassword);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
