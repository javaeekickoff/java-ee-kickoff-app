package org.example.kickoff.plumbing.jaspic;

import static java.util.Collections.unmodifiableList;
import static javax.security.auth.message.AuthStatus.SEND_FAILURE;
import static javax.security.auth.message.AuthStatus.SUCCESS;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.omnifaces.util.Utils.coalesce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.example.kickoff.auth.Authenticator;
import org.example.kickoff.auth.LoginBean;
import org.example.kickoff.plumbing.cdi.Beans;

// import org.example.kickoff.cdi.Beans;

/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule extends HttpServerAuthModule {
	
	private static final String AUTHENTICATOR_SESSION_NAME = "org.example.kickoff.jaspic.Authenticator";
	
	
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
		if (isLoginRequest(request, clientSubject, handler)) {
			return SUCCESS;
		}
		
		if (isProtectedResource) {
			response.setStatus(SC_FORBIDDEN); // Note: JASPIC doesn't cause this to be set automatically
			return SEND_FAILURE; // For now, support redirect to login later
		}

		// Not already authenticated, no login request and no protected resource. Just continue.
		return SUCCESS;
	}
	
	private boolean notNull(Object... objects) {
		return coalesce(objects) != null;
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
	
	private boolean isLoginRequest(HttpServletRequest request, Subject clientSubject, CallbackHandler handler) {
		Delegators delegators = tryGetDelegators();
		
		// This SAM is supposed to work following a call to HttpServletRequest#authenticate. Such call is in-context of the component executing it,
		// which *should* have the correct CDI contexts active to obtain our CDI delegators.
		//
		// In case this SAM is triggered at the very beginning of a request (which is before even the first Servlet Filter kicks in), those CDI
		// contexts are typically not (fully) available.
		if (delegators != null) {
			
			Authenticator authenticator = delegators.getAuthenticator();
			LoginBean loginBean = delegators.getLoginBean();
			
			if (notNull(loginBean.getLoginUserName(), loginBean.getLoginPassword())) {
				
				authenticator.authenticate(loginBean.getLoginUserName(), loginBean.getLoginPassword());
				
				notifyContainerAboutLogin(request, clientSubject, handler, authenticator.getUserName(), authenticator.getApplicationRoles());
				
				// Since CDI is not universally available when this SAM is called at the beginning of a request, we
				// explicitly store our authenticator bean in the HTTP session, so we can later on (the next requests) retrieve it
				// to re-authenticate.
				request.getSession().setAttribute(
					AUTHENTICATOR_SESSION_NAME, 
					new AuthenticationData(authenticator.getUserName(), authenticator.getApplicationRoles())
				);
				
				return true;
			}
		}
		
		return false;
	}
	
	
	private Delegators tryGetDelegators() {

		try {
			BeanManager beanManager = Beans.getBeanManager();

			Authenticator authenticator = Beans.getReference(Authenticator.class, beanManager);
			LoginBean loginBean = Beans.getReference(LoginBean.class, beanManager);

			// Some containers might give us the bean, but then don't allow us the reference it if called
			// in the "wrong" context.
			loginBean.getLoginPassword();

			return new Delegators(authenticator, loginBean);
		} catch (Exception e) {
			return null;
		}
	}
	
	private void notifyContainerAboutLogin(HttpServletRequest request, Subject clientSubject, CallbackHandler handler, String userName, List<String> roles) {
		
		// Create a handler (kind of directive) to add the caller principal (AKA user principal =basically user name, or user id) that
		// the authenticator provides.
		//
		// This will be the name of the principal returned by e.g. HttpServletRequest#getUserPrincipal
		CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, userName);
		
		// Create a handler to add the groups (AKA roles) that the authenticator provides. 
		//
		// This is what e.g. HttpServletRequest#isUserInRole and @RolesAllowed for
		GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, roles.toArray(new String[roles.size()]));

		
		try {
			// Execute the handlers we created above. 
			//
			// This will typically add the provided principal and roles in an application server specific way to the JAAS Subject.
			// (it could become entries in a hash table inside the subject, or individual principles, or nested group principles etc.
			handler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });
			
		} catch (IOException | UnsupportedCallbackException e) {
			// Should not happen
			throw new IllegalStateException(e);
		}
	}
	
	private static class Delegators {

		private final Authenticator authenticator;
		private final LoginBean loginBean;

		public Delegators(Authenticator authenticator, LoginBean loginBean) {
			this.authenticator = authenticator;
			this.loginBean = loginBean;
		}

		public Authenticator getAuthenticator() {
			return authenticator;
		}

		public LoginBean getLoginBean() {
			return loginBean;
		}
	}
	
	private static class AuthenticationData {

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