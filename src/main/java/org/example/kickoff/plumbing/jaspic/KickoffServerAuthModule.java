package org.example.kickoff.plumbing.jaspic;

import static java.util.Collections.unmodifiableList;
import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static javax.security.auth.message.AuthStatus.SEND_FAILURE;
import static javax.security.auth.message.AuthStatus.SUCCESS;
import static org.example.kickoff.plumbing.jaspic.KickoffServerAuthModule.LoginResult.LOGIN_FAILURE;
import static org.example.kickoff.plumbing.jaspic.KickoffServerAuthModule.LoginResult.LOGIN_SUCCESS;
import static org.example.kickoff.plumbing.jaspic.KickoffServerAuthModule.LoginResult.NO_LOGIN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthStatus;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.example.kickoff.plumbing.cdi.Beans;
import org.example.kickoff.plumbing.jaspic.request.CookieDAO;
import org.example.kickoff.plumbing.jaspic.request.HttpServletRequestDelegator;
import org.example.kickoff.plumbing.jaspic.request.RequestDAO;
import org.example.kickoff.plumbing.jaspic.request.RequestData;
import org.example.kickoff.plumbing.jaspic.user.Authenticator;
import org.example.kickoff.plumbing.jaspic.user.TokenAuthenticator;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordAuthenticator;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordProvider;


/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule extends HttpServerAuthModule {
	
	private static final String AUTHENTICATOR_SESSION_NAME = "org.example.kickoff.jaspic.Authenticator";
	
	private final RequestDAO requestDAO = new RequestDAO();
	private final CookieDAO cookieDAO = new CookieDAO();
	
	static enum LoginResult {
		LOGIN_SUCCESS,
		LOGIN_FAILURE,
		NO_LOGIN		
	}
	
	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, Subject clientSubject, CallbackHandler handler, boolean isProtectedResource) {
				
		// Check to see if we're already authenticated.
		//
		// With JASPIC, the container doesn't remember authentication data between requests and we have thus have to
		// re-authenticate before every request.
		if (canReAuthenticate(request, clientSubject, handler)) {
			return SUCCESS;
		}
		
		
		// Check to see if this is a request from user code to login
		//
		// In the case of this SAM, it means a managed bean has called request#authenticate and the login bean
		// contains a non-null user name and password.
		switch (isLoginRequest(request, response, clientSubject, handler)) {
		
			case LOGIN_SUCCESS:
		
				// Check if there's a previously saved request. This is the case if a protected request was
				// accessed and the user was subsequently redirected to the login page.
				RequestData requestData = requestDAO.get(request);
				if (requestData != null) {
					redirect(response, requestData.getFullRequestURL());
				} 
				
				return SUCCESS;
				
			case LOGIN_FAILURE:
				
				// End request processing and don't try to process the handler
				//
				// Note: Most JASPIC implementations don't distinguish between return codes and only check if return is SUCCESS or not
				// Note: In the case of this SAM, login is called following a request#authenticate only, so in that case a non-SUCCESS
				//       return only means no to process the handler.
				return SEND_FAILURE; 
		}
		
		
		// Check to see if this request is to a protected resource
		//
		// We'll save the current request here, so we can redirect to the original URL after
		// authentication succeeds and when we start processing that URL wrap the request
		// with one containing the original headers, cookies, etc.
		if (isProtectedResource) {
			
			requestDAO.save(request);
			redirect(response, getBaseURL(request) + "/login.xhtml");
						
			return SEND_CONTINUE; // End request processing for this request and don't try to process the handler
		}

		// Not already authenticated, no login request and no protected resource. Just continue.
		return SUCCESS;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		
		// See if there was a saved request that matches the current request and restore
		// that request by wrapping the current request.
		//
		// Note that it doesn't seem possible to do this in a portable way in validateHttpRequest
		RequestData requestData = requestDAO.get(servletRequest);
		Cookie cookie = cookieDAO.get(servletRequest);
		
		if (requestData != null) {
			
			if (requestData.matchesRequest(servletRequest)) {
				servletRequest = new HttpServletRequestDelegator(servletRequest, requestData);
				requestDAO.remove(servletRequest);
			} else if (cookie != null && servletRequest.getRequestURL().toString().equals(getBaseURL(servletRequest) + "/login.xhtml")) {
				// There is requestData available and a cookie, as well as a request to the login page.
				// We use this login page as a cue to do login via the cookie.
				if (servletRequest.authenticate((HttpServletResponse) response)) {
					// If authentication succeeded, don't process the request to the login page.
					return;
				}
			}
		}
		
		chain.doFilter(servletRequest, response);
	}
	
	@Override
	public AuthStatus logout(HttpServletRequest request, HttpServletResponse response, Subject clientSubject) {
		
		// If there's a "remember me" cookie present, remove it.
		if (cookieDAO.get(request) != null) {
			cookieDAO.remove(request, response);
			Delegators delegators = tryGetDelegators();
			if (delegators != null && delegators.getTokenAuthenticator() != null) {
				delegators.getTokenAuthenticator().removeLoginToken();			
			}				
		}
		
		return SEND_CONTINUE;
	}
	
	private boolean canReAuthenticate(HttpServletRequest request, Subject clientSubject, CallbackHandler handler) {
		
		HttpSession session = request.getSession(false);
		if (session != null) {
			AuthenticationData authenticationData = (AuthenticationData) session.getAttribute(AUTHENTICATOR_SESSION_NAME);
			if (authenticationData != null) {
				notifyContainerAboutLogin(request, clientSubject, handler, authenticationData.getUserName(), authenticationData.getApplicationRoles());
				
				return true;
			}
		}
		
		return false;
	}
	
	private LoginResult isLoginRequest(HttpServletRequest request, HttpServletResponse response, Subject clientSubject, CallbackHandler handler) {
		Delegators delegators = tryGetDelegators();
		
		// This SAM is supposed to work following a call to HttpServletRequest#authenticate. Such call is in-context of the component executing it,
		// which *should* have the correct CDI contexts active to obtain our CDI delegators.
		//
		// In case this SAM is triggered at the very beginning of a request (which is before even the first Servlet Filter kicks in), those CDI
		// contexts are typically not (fully) available.
		if (delegators != null) {
			
			UsernamePasswordAuthenticator usernamePasswordAuthenticator = delegators.getAuthenticator();
			TokenAuthenticator tokenAuthenticator =	delegators.getTokenAuthenticator();
			
			UsernamePasswordProvider provider = delegators.getProvider();
			Cookie cookie = cookieDAO.get(request);
			
			Authenticator authenticator = null;
			boolean authenticated = false;
			if (notNull(provider.getLoginUserName(), provider.getLoginPassword())) {
				authenticated = usernamePasswordAuthenticator.authenticate(provider.getLoginUserName(), provider.getLoginPassword());
				authenticator = usernamePasswordAuthenticator;
			} else if (cookie != null && tokenAuthenticator != null) {
				authenticated = tokenAuthenticator.authenticate(cookie.getValue());
				
				if (!authenticated) {
					// Invalid cookie, remove it
					cookieDAO.remove(request, response);
					
					// Authentication via cookie is an implicit login, so if it fails we just ignore it
					// so the flow falls-through to the normal login.
					return NO_LOGIN;
				} else {
					authenticator = tokenAuthenticator;
				}
			} else {
				return NO_LOGIN;
			}
			
			if (authenticated) {
				
				notifyContainerAboutLogin(request, clientSubject, handler, authenticator.getUserName(), authenticator.getApplicationRoles());
				
				// Since CDI is not universally available when this SAM is called at the beginning of a request, we
				// explicitly store our authenticator bean in the HTTP session, so we can later on (the next requests) retrieve it
				// to re-authenticate.
				request.getSession().setAttribute(
					AUTHENTICATOR_SESSION_NAME, 
					new AuthenticationData(authenticator.getUserName(), authenticator.getApplicationRoles())
				);
				
				if (tokenAuthenticator != null) {
					cookieDAO.save(request, response, tokenAuthenticator.generateLoginToken());
				}
				
				return LOGIN_SUCCESS;
			} else {
				return LOGIN_FAILURE;
			}
			
		}
		
		return NO_LOGIN;
	}
	
	private Delegators tryGetDelegators() {

		try {
			BeanManager beanManager = Beans.getBeanManager();

			UsernamePasswordAuthenticator usernamePasswordAuthenticator = Beans.getReference(UsernamePasswordAuthenticator.class, beanManager);
			UsernamePasswordProvider provider = Beans.getReference(UsernamePasswordProvider.class, beanManager);
			
			TokenAuthenticator tokenAuthenticator = Beans.getReference(TokenAuthenticator.class, beanManager);

			// Some containers might give us the bean, but then don't allow us to reference it if called
			// in the "wrong" context.
			provider.getLoginPassword();

			return new Delegators(usernamePasswordAuthenticator, tokenAuthenticator, provider);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static class Delegators {

		private final UsernamePasswordAuthenticator authenticator;
		private final TokenAuthenticator tokenAuthenticator;
		private final UsernamePasswordProvider provider;

		public Delegators(UsernamePasswordAuthenticator authenticator, TokenAuthenticator tokenAuthenticator, UsernamePasswordProvider provider) {
			this.authenticator = authenticator;
			this.tokenAuthenticator = tokenAuthenticator;
			this.provider = provider;
		}

		public UsernamePasswordAuthenticator getAuthenticator() {
			return authenticator;
		}

		public UsernamePasswordProvider getProvider() {
			return provider;
		}

		public TokenAuthenticator getTokenAuthenticator() {
			return tokenAuthenticator;
		}
	}
	
	private class AuthenticationData {

		private final String userName;
		private final List<String> applicationRoles;

		public AuthenticationData(String userName, List<String> applicationRoles) {
			this.userName = userName;
			this.applicationRoles = unmodifiableList(new ArrayList<>(applicationRoles));
		}

		public String getUserName() {
			return userName;
		}

		public List<String> getApplicationRoles() {
			return applicationRoles;
		}
	}

	
}