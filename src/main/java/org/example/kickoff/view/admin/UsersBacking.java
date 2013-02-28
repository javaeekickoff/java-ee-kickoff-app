package org.example.kickoff.view.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.example.kickoff.business.UserService;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;
import org.primefaces.event.RowEditEvent;

@Named
@RequestScoped
public class UsersBacking {

	@EJB
	private UserService userService;

	private List<User> users;
	private List<User> filteredUsers;
	private List<Group> groups;
	private SelectItem[] groupFilterOptions;

	@PostConstruct
	private void onPreload() {
		users = userService.getUsers();
		groups = Arrays.asList(Group.values());

		List<SelectItem> list = new ArrayList<>();

		list.add(new SelectItem("", "Select"));
		for (Group group : groups) {
			SelectItem item = new SelectItem(group, group.toString());
			list.add(item);
		}

		groupFilterOptions = list.toArray(new SelectItem[list.size()]);
	}
	
	public void onEdit(RowEditEvent event) {
		User user = (User) event.getObject();

		userService.update(user);
	}

	public List<User> getUsers() {
		return users;
	}

	public List<User> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<User> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public SelectItem[] getGroupsFilter() {
		return groupFilterOptions;
	}

}
