package org.example.kickoff.view.admin;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;
import org.primefaces.event.RowEditEvent;


@ManagedBean
@ViewScoped
public class UsersBacking {

	@EJB
	private UserService userService;

	private List<User> users;

	private List<Group> groups;

	@PostConstruct
	private void onPreload() {
		users = userService.getUsers();
		groups = Arrays.asList(Group.values());
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Group> getGroups() {
		return groups;
	}

    public void onEdit(RowEditEvent event) {
    	User user = (User) event.getObject();

        userService.update(user);
    }

}
