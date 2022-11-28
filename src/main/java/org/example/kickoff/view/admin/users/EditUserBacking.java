package org.example.kickoff.view.admin.users;

import static org.omnifaces.util.Faces.redirect;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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

	public void save() throws IOException {
		userService.update(user);
		redirect("admin/users");
	}

	public User getUser() {
		return user;
	}

}