package org.example.kickoff.view.auth;

import static org.omnifaces.util.Faces.invalidateSession;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;

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