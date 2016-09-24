package org.example.kickoff.view.admin;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.example.kickoff.view.PagedDataModel;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.persistence.model.dto.SortFilterPage;
import org.primefaces.model.SortOrder;

@Named
@ViewScoped
public class UsersBacking implements Serializable {

	private static final long serialVersionUID = 1L;

	private PagedDataModel<User> users;

	@Inject
	private UserService userService;

	@PostConstruct
	public void init() {
		users = new PagedDataModel<User>("created", SortOrder.DESCENDING, "id", "email", "fullName") {
			private static final long serialVersionUID = 1L;

			@Override
			public List<User> load(SortFilterPage page, boolean countNeedsUpdate) {
				return userService.getByPage(page, countNeedsUpdate);
			}
		};
	}

	public void delete(User user) {
		userService.delete(user);
	}

	public DataModel<User> getUsers() {
		return users;
	}

}