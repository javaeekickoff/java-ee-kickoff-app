package org.example.kickoff.view;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;

import org.example.kickoff.config.auth.KickoffCallerPrincipal;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.Role;
import org.example.kickoff.model.User;

@Named
@SessionScoped
public class ActiveUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Boolean> is = new ConcurrentHashMap<>();
	private Map<String, Boolean> can = new ConcurrentHashMap<>();
	private Map<String, Boolean> canView = new ConcurrentHashMap<>();

	private User activeUser;

	@Inject
	private SecurityContext securityContext;

	public User get() { // For use in backing beans.
		if (activeUser == null) {
			activeUser = securityContext
				.getPrincipalsByType(KickoffCallerPrincipal.class).stream()
				.map(KickoffCallerPrincipal::getUser)
				.findAny().orElse(null);
		}

		return activeUser;
	}

	public boolean isPresent() { // For use in both backing beans and EL #{activeUser.present}
		return get() != null;
	}

	public Long getId() { // For use in both backing beans and EL #{activeUser.id}
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
		return isPresent() && can.computeIfAbsent(role, r -> activeUser.getRoles().stream().map(Role::name).anyMatch(r::equalsIgnoreCase));
	}

	public boolean canView(String path) { // For use in EL #{activeUser.canView('admin/users')}
		return canView.computeIfAbsent(path, p -> securityContext.hasAccessToWebResource(p.startsWith("/") ? p : ("/" + p), "GET"));
	}

	@Override
	public String toString() { // Must print friendly name in EL #{activeUser}.
		return isPresent() ? activeUser.getFullName() : null;
	}

}