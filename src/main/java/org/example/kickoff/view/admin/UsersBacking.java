package org.example.kickoff.view.admin;

import static org.omnifaces.optimusfaces.model.PagedDataModel.pagedDataModelFor;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.optimusfaces.model.PagedDataModel;

@Named
@ViewScoped
public class UsersBacking implements Serializable {

	private static final long serialVersionUID = 1L;

	private PagedDataModel<User> users;

	@Inject
	private UserService userService;

	@PostConstruct
	public void init() {
		users = pagedDataModelFor(User.class)
                      .defaultSortField("created")
                      .filterableFields("id", "email", "fullName")
                      .dataLoader((page, count) -> userService.getByPage(page, count))
                      .build();
	}

	public void delete(User user) {
		userService.delete(user);
	}

	public DataModel<User> getUsers() {
		return users;
	}

}