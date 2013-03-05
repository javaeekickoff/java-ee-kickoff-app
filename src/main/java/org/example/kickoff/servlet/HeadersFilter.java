package org.example.kickoff.servlet;

import static java.util.concurrent.TimeUnit.DAYS;
import static javax.faces.application.ResourceHandler.RESOURCE_IDENTIFIER;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omnifaces.filter.HttpFilter;

/**
 * This filter sets all required headers for the web app. It currently mainly deals with encoding and caching headers.
 *
 * @author Arjan Tijms
 *
 */
@WebFilter(urlPatterns="/*", dispatcherTypes={REQUEST, FORWARD})
public class HeadersFilter extends HttpFilter {

	private static final String ENCODING = "UTF-8";
	private static final String CONTENT_TYPE = "text/html;charset=" + ENCODING;
	private static final long EXPIRE_TIME = DAYS.toMillis(7);

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain)
		throws IOException, ServletException {

		if (!request.getServletPath().startsWith(RESOURCE_IDENTIFIER)) {
			
			// Dynamically generated pages should not be cached.
			
			// Encoding
			response.setContentType(CONTENT_TYPE);
			request.setCharacterEncoding(ENCODING);

			// HTTP 1.0 (Deprecated)
			response.setHeader("Pragma", "no-cache"); 
			
			// HTTP 1.1
			response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
			
			// Enforces validation
			response.setDateHeader("Expires", 0); 
			response.setDateHeader("Last-Modified", System.currentTimeMillis());
			
		} else { 
			
			// Resources can be cached
			
			response.setHeader("Cache-Control", "public, max-age=" + (EXPIRE_TIME / 1000));
			response.setDateHeader ("Expires", System.currentTimeMillis() + EXPIRE_TIME);
			
			// Pragma is being explicitly set here to prevent a downstream overriding it.
			response.setHeader("Pragma", "");
		}
		
		chain.doFilter(request, response);
		
		return;
	}

}
