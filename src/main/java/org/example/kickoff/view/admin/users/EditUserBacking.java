package org.example.kickoff.view.admin.users;

import static org.omnifaces.util.Faces.redirect;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class EditUserBacking {

	@Inject @Param(name="id")
	private User user;

	@Inject
	private UserService userService;

	public void save() {
		userService.update(user);
		redirect("admin/users");
	}

	public User getUser() {
		return user;
	}

}