package org.example.kickoff.plumbing.jaspic;

import static org.example.kickoff.plumbing.jaspic.HttpServerAuthModule.IS_AUTHENTICATION_KEY;
import static org.example.kickoff.plumbing.jaspic.HttpServerAuthModule.IS_LOGOUT_KEY;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class Jaspic {

	private Jaspic() {}
	
	public static void logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getSession().invalidate();
			request.logout();
			
			// Hack to signal to the SAM that we are logging out
			request.setAttribute(IS_LOGOUT_KEY, true);
			request.authenticate(response);
		} catch (ServletException | IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			request.removeAttribute(IS_LOGOUT_KEY);
		}
	}
	
	public static boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setAttribute(IS_AUTHENTICATION_KEY, true);
			return request.authenticate(response);
		} catch (ServletException | IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			request.removeAttribute(IS_AUTHENTICATION_KEY);
		}
	}
	
}
