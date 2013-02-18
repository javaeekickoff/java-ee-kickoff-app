package org.example.kickoff.view.admin;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;


@ManagedBean
@ViewScoped
public class UsersBacking {

	@EJB
	private UserService userService;

	private List<User> users;

	@PostConstruct
	private void onPreload() {
		users = userService.getUsers();
	}

	public List<User> getUsers() {
		return users;
	}

}
