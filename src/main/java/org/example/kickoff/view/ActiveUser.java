package org.example.kickoff.view;

import static java.util.Collections.emptyList;

import java.util.List;

import javax.enterprise.inject.Typed;

import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;
import org.example.kickoff.model.producer.ActiveUserProducer;

/**
 * This is produced by {@link ActiveUserProducer}.
 * This is available in EL by #{activeUser}.
 * This is available in backing beans by {@code @Inject}.
 */
@Typed
public class ActiveUser {

	private User activeUser;

	public ActiveUser() {
		// C'tor for proxy.
	}

	public ActiveUser(User user) { // Used by ActiveUserProducer.
		activeUser = user;
	}

	public boolean isPresent() { // For use in both backing beans and EL.
		return activeUser != null;
	}

	public Long getId() { // For use in backing beans in order to get concrete User.
		return isPresent() ? activeUser.getId() : null;
	}

	public List<Group> getGroups() { // For use in backing beans.
		return isPresent() ? activeUser.getGroups() : emptyList();
	}

	public boolean is(String group) { // For use in EL #{activeUser.is('ADMIN')}
		return getGroups().contains(Group.valueOf(group));
	}

	public boolean can(String role) { // For use in EL #{activeUser.can('VIEW_ADMIN_PAGES')}
		return isPresent() && activeUser.getRolesAsStream().anyMatch(r -> r.name().equals(role));
	}

	@Override
	public String toString() { // Must print friendly name in EL #{activeUser}.
		return isPresent() ? activeUser.getFullName() : null;
	}

}