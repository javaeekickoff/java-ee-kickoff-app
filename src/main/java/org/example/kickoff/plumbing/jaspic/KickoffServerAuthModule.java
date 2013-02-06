package org.example.kickoff.plumbing.jaspic;

import static javax.security.auth.message.AuthStatus.SUCCESS;
import static org.omnifaces.util.Utils.coalesce;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.kickoff.auth.Authenticator;
import org.example.kickoff.auth.LoginBean;
import org.example.kickoff.plumbing.cdi.Beans;

// import org.example.kickoff.cdi.Beans;

/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule extends HttpServerAuthModule {
	
	
	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, Subject clientSubject, CallbackHandler handler) {
		try {
			
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
						
					return SUCCESS;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}
	
	private boolean notNull(Object... objects) {
		return coalesce(objects) != null;
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
	
	private void notifyContainerAboutLogin(HttpServletRequest request, Subject clientSubject, CallbackHandler handler, String userName, List<String> roles) {
		
		// Create a handler (kind of directive) to add the caller principal (AKA user principal =basically user name, or user id) that
		// the authenticator provides.
		//
		// This will be the name of the principal returned by e.g. HttpServletRequest#getUserPrincipal
		CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, userName);
		
		// Create a handler to add the groups (AKA roles) that the authenticator provides. 
		//
		// This is what e.g. HttpServletRequest#isUserInRole and @RolesAllowed for
		GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, (String[]) roles.toArray());

		
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