package org.example.kickoff.view.admin.users;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.util.Messages;

@ManagedBean
@ViewScoped
public class DeleteUserBacking {

	private User user;

	@EJB
	private UserService userService;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void deleteUser() {
		userService.delete(user);

		Messages.addGlobalInfo("User {0} deleted successfully", user.getEmail());
	}
}
