package org.example.kickoff.view.admin.users;

import static org.example.kickoff.validator.Demo.canUpdate;
import static org.omnifaces.util.Messages.addGlobalInfo;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;

@ManagedBean
@ViewScoped
public class DeleteUserBacking {

	private User user;

	@EJB
	private UserService userService;

	public void deleteUser() {
		if (canUpdate(user)) {
			userService.delete(user);
			addGlobalInfo("User {0} deleted successfully", user.getEmail());
		}
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
