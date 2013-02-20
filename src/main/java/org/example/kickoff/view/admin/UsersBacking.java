package org.example.kickoff.view.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

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

	public void onEdit(RowEditEvent event) {
		User user = (User) event.getObject();

		userService.update(user);
	}

}
