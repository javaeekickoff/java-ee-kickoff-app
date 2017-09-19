package org.example.kickoff.view.filter;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.example.kickoff.view.ActiveLocale.COOKIE_MAX_AGE_IN_DAYS;
import static org.example.kickoff.view.ActiveLocale.COOKIE_NAME;
import static org.omnifaces.util.Servlets.addResponseCookie;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.example.kickoff.view.ActiveLocale;
import org.omnifaces.filter.HttpFilter;
import org.omnifaces.util.Servlets;

@WebFilter(filterName = "localeFilter")
public class LocaleFilter extends HttpFilter {

	@Inject
	private ActiveLocale activeLocale;

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {

		if (Servlets.isFacesResourceRequest(request)) {
			chain.doFilter(request, response);
		}
		else if (activeLocale.isExplicitlyRequested()) {
			if (activeLocale.isChanged() && !isFacesEvent(request)) { // Never set locale cookie on ajax or unload events.
				addResponseCookie(request, response, COOKIE_NAME, activeLocale.getLanguageTag(), "/", (int) DAYS.toSeconds(COOKIE_MAX_AGE_IN_DAYS));
			}

			if (activeLocale.isDefaultLocale()) {
				response.sendRedirect(request.getContextPath() + activeLocale.getUri());
			}
			else {
				request.getRequestDispatcher(activeLocale.getUri()).forward(request, response);
			}
		}
		else if (!activeLocale.isDefaultLocale()) {
			response.sendRedirect(request.getContextPath() + activeLocale.getPath() + activeLocale.getUri());
		}
		else {
			chain.doFilter(request, response);
		}
	}

	private static boolean isFacesEvent(HttpServletRequest request) {
		return request.getParameter("javax.faces.behavior.event") != null || request.getParameter("omnifaces.event") != null;
	}

}