package org.example.kickoff.view.auth;

import static org.omnifaces.util.Faces.invalidateSession;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.ServletException;

import org.omnifaces.util.Faces;

@Named
@RequestScoped
public class LogoutBacking {

	public void logout() throws ServletException, IOException {
		Faces.logout();
		invalidateSession();
		addFlashGlobalWarn("auth.message.warn.logged_out");
		redirect("");
	}

}