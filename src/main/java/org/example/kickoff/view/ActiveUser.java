package org.example.kickoff.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Typed;

import org.example.kickoff.model.Group;
import org.example.kickoff.model.Role;
import org.example.kickoff.model.User;
import org.example.kickoff.model.producer.ActiveUserProducer;
import org.omnifaces.config.WebXml;

/**
 * This is produced by {@link ActiveUserProducer}.
 * This is available in EL by #{activeUser}.
 * This is available in backing beans by {@code @Inject}.
 */
@Typed
public class ActiveUser {

	private Map<String, Boolean> is = new ConcurrentHashMap<>();
	private Map<String, Boolean> can = new ConcurrentHashMap<>();
	private Map<String, Boolean> canView = new ConcurrentHashMap<>();

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

	public boolean hasGroup(Group group) { // For use in backing beans.
		return isPresent() && activeUser.getGroups().contains(group);
	}

	public boolean hasRole(Role role) { // For use in backing beans.
		return isPresent() && activeUser.getRoles().contains(role);
	}

	public boolean is(String group) { // For use in EL #{activeUser.is('ADMIN')}
		return isPresent() && is.computeIfAbsent(group, g -> activeUser.getGroups().stream().map(Group::name).anyMatch(g::equalsIgnoreCase));
	}

	public boolean can(String role) { // For use in EL #{activeUser.can('VIEW_ADMIN_PAGES')}
		return isPresent() && can.computeIfAbsent(role, r -> activeUser.getRolesAsStrings().stream().anyMatch(r::equalsIgnoreCase));
	}

	public boolean canView(String path) { // For use in EL #{activeUser.canView('admin/users')}
		return canView.computeIfAbsent(path, this::isAccessAllowed);
	}

	private boolean isAccessAllowed(String path) {
		String uri = path.startsWith("/") ? path : ("/" + path);

		if (WebXml.INSTANCE.isAccessAllowed(uri, null)) {
			return true;
		}

		return isPresent() && activeUser.getRolesAsStrings().stream().anyMatch(r -> WebXml.INSTANCE.isAccessAllowed(uri, r));
	}

	@Override
	public String toString() { // Must print friendly name in EL #{activeUser}.
		return isPresent() ? activeUser.getFullName() : null;
	}

}