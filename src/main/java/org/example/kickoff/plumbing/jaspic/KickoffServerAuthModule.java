package org.example.kickoff.plumbing.jaspic;

import static javax.security.auth.message.AuthStatus.SEND_CONTINUE;
import static javax.security.auth.message.AuthStatus.SEND_FAILURE;
import static javax.security.auth.message.AuthStatus.SUCCESS;
import static org.example.kickoff.plumbing.jaspic.dto.LoginResult.LOGIN_FAILURE;
import static org.example.kickoff.plumbing.jaspic.dto.LoginResult.LOGIN_SUCCESS;
import static org.example.kickoff.plumbing.jaspic.dto.LoginResult.NO_LOGIN;
import static org.example.kickoff.plumbing.jaspic.request.RequestCopier.copy;

import java.io.IOException;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.example.kickoff.auth.Authenticator;
import org.example.kickoff.auth.LoginBean;
import org.example.kickoff.plumbing.cdi.Beans;
import org.example.kickoff.plumbing.jaspic.dto.AuthenticationData;
import org.example.kickoff.plumbing.jaspic.dto.Delegators;
import org.example.kickoff.plumbing.jaspic.dto.LoginResult;
import org.example.kickoff.plumbing.jaspic.request.HttpServletRequestDelegator;
import org.example.kickoff.plumbing.jaspic.request.RequestData;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordAuthenticator;
import org.example.kickoff.plumbing.jaspic.user.UsernamePasswordProvider;


/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule extends HttpServerAuthModule {
	
	private static final String AUTHENTICATOR_SESSION_NAME = "org.example.kickoff.jaspic.Authenticator";
	private static final String ORIGINAL_REQUEST_DATA_SESSION_NAME = "org.example.kickoff.jaspic.original.request";
	
	
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
		switch (isLoginRequest(request, clientSubject, handler)) {
		
			case LOGIN_SUCCESS:
		
				// Check if there's a previously saved request. This is the case if a protected request was
				// accessed and the user was subsequently redirected to the login page.
				RequestData requestData = getSavedRequestData(request);
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
			
			saveRequest(request);
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
		RequestData requestData = getSavedRequestData(servletRequest);
		if (requestData != null && requestData.matchesRequest(servletRequest)) {
			servletRequest = new HttpServletRequestDelegator(servletRequest, requestData);
			removeSavedRequest(servletRequest);
		}
		
		chain.doFilter(servletRequest, response);
	}
	
	public void saveRequest(HttpServletRequest request) {
		request.getSession().setAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME, copy(request));
	}
	
	public RequestData getSavedRequestData(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		
		return (RequestData) session.getAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME);
	}
	
	public void removeSavedRequest(HttpServletRequest request) {
		request.getSession().removeAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME);
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
	
	private LoginResult isLoginRequest(HttpServletRequest request, Subject clientSubject, CallbackHandler handler) {
		Delegators delegators = tryGetDelegators();
		
		// This SAM is supposed to work following a call to HttpServletRequest#authenticate. Such call is in-context of the component executing it,
		// which *should* have the correct CDI contexts active to obtain our CDI delegators.
		//
		// In case this SAM is triggered at the very beginning of a request (which is before even the first Servlet Filter kicks in), those CDI
		// contexts are typically not (fully) available.
		if (delegators != null) {
			
			UsernamePasswordAuthenticator authenticator = delegators.getAuthenticator();
			UsernamePasswordProvider provider = delegators.getProvider();
			
			if (notNull(provider.getLoginUserName(), provider.getLoginPassword())) {
				
				if (authenticator.authenticate(provider.getLoginUserName(), provider.getLoginPassword())) {
				
					notifyContainerAboutLogin(request, clientSubject, handler, authenticator.getUserName(), authenticator.getApplicationRoles());
					
					// Since CDI is not universally available when this SAM is called at the beginning of a request, we
					// explicitly store our authenticator bean in the HTTP session, so we can later on (the next requests) retrieve it
					// to re-authenticate.
					request.getSession().setAttribute(
						AUTHENTICATOR_SESSION_NAME, 
						new AuthenticationData(authenticator.getUserName(), authenticator.getApplicationRoles())
					);
					
					return LOGIN_SUCCESS;
				} else {
					return LOGIN_FAILURE;
				}
			}
		}
		
		return NO_LOGIN;
	}
	
	private Delegators tryGetDelegators() {

		try {
			BeanManager beanManager = Beans.getBeanManager();

			Authenticator authenticator = Beans.getReference(Authenticator.class, beanManager);
			LoginBean loginBean = Beans.getReference(LoginBean.class, beanManager);

			// Some containers might give us the bean, but then don't allow us to reference it if called
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

	
}