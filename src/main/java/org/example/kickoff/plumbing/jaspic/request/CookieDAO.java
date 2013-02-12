package org.example.kickoff.plumbing.jaspic.request;

import static java.util.concurrent.TimeUnit.DAYS;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieDAO {
	
	private static final String COOKIE_NAME = "kickoff_login_token";
	private static final int MAX_AGE = (int) DAYS.toSeconds(14);

	public void save(HttpServletRequest request, HttpServletResponse response, String value) {
		Cookie cookie = new Cookie(COOKIE_NAME, value);
		cookie.setMaxAge(MAX_AGE);
		cookie.setPath(request.getContextPath());
		
		if ("localhost".equals(request.getServerName())) {
			cookie.setDomain(request.getServerName());
		}

		response.addCookie(cookie);
	}
	
	public Cookie get(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (COOKIE_NAME.equals(cookie.getName()) && !isEmpty(cookie)) {
					return cookie;
				}
			}
		}

		return null;
	}

	public void remove(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		cookie.setPath(request.getContextPath());
		
		if ("localhost".equals(request.getServerName())) {
			cookie.setDomain(request.getServerName());
		}

		response.addCookie(cookie);
	}
	
	private boolean isEmpty(Cookie cookie) {
		return cookie.getValue() == null || cookie.getValue().trim().isEmpty();
	}
	
}
