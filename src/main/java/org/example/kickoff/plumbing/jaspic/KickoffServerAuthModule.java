package org.example.kickoff.plumbing.jaspic;

import static javax.security.auth.message.AuthStatus.SUCCESS;
import static org.omnifaces.util.Utils.coalesce;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.naming.InitialContext;
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
import org.example.kickoff.plumbing.cdi.BeanResolver;

// import org.example.kickoff.cdi.Beans;

/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule extends HttpServerAuthModule {
	
	
	@Override
	public AuthStatus validateHttpRequest(HttpServletRequest request, HttpServletResponse response, Subject clientSubject, CallbackHandler handler) {
		try {
			
			InitialContext initialContext = new InitialContext();
			BeanResolver beanResolver = (BeanResolver) initialContext.lookup("java:global/java-ee-kickoff-app/BeanResolver");
			
			// "SEVERE: No valid EE environment for injection of org.example.kickoff.auth.Authenticator" if getting reference on
			// GlassFish.
			Authenticator authenticator = beanResolver.getInstance(Authenticator.class, SessionScoped.class);
			
			LoginBean loginBean = beanResolver.getInstance(LoginBean.class, RequestScoped.class);
			
			if (coalesce(authenticator, loginBean) != null && coalesce(loginBean.getLoginUserName(), loginBean.getLoginPassword()) != null) {
				authenticator.authenticate(loginBean.getLoginUserName(), loginBean.getLoginPassword());
		
				// Create a handler (kind of directive) to add the caller principal (AKA
				// user principal) "test" (=basically user name, or user id)
				// This will be the name of the principal returned by e.g.
				// HttpServletRequest#getUserPrincipal
				CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, authenticator.getUserName());
		
				// Create a handler to add the group (AKA role) "architect"
				// This is what e.g. HttpServletRequest#isUserInRole and @RolesAllowed
				// test for
				GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, (String[]) authenticator.getApplicationRoles().toArray());
		
				// Execute the handlers we created above. This will typically add the
				// "test" principal and the "architect"
				// role in an application server specific way to the JAAS Subject.
				try {
					handler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });
					
					return SUCCESS;
				}
				catch (IOException | UnsupportedCallbackException e) {
					e.printStackTrace();
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return AuthStatus.SEND_CONTINUE;
	}

	
}