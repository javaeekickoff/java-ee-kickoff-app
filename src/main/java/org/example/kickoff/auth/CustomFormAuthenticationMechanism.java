package org.example.kickoff.auth;
import javax.enterprise.inject.spi.CDI;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.authentication.mechanism.http.HttpMessageContext;
import javax.security.authentication.mechanism.http.annotation.AutoApplySession;
import javax.security.authentication.mechanism.http.annotation.LoginToContinue;
import javax.security.authentication.mechanism.http.annotation.RememberMe;
import javax.security.identitystore.IdentityStore;
import javax.security.identitystore.credential.CallerOnlyCredential;
import javax.security.identitystore.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Authentication mechanism that authenticates according to the Servlet spec defined FORM
 * authentication mechanism. See Servlet spec for further details.
 * 
 * @author Arjan Tijms
 *
 */
@AutoApplySession // For "is user already logged-in"
@RememberMe(
	cookieMaxAgeSeconds = 60 * 60 * 24 * 14, // 14 days
	cookieName = "kickoff_login_token"
)
@LoginToContinue(
	loginPage="/login?new=false",
	errorPage="",
	useForwardToLogin = false
)
public class CustomFormAuthenticationMechanism implements HttpAuthenticationMechanism {
    
	@Override
	public AuthStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthException {
        
        if (hasCredential(httpMessageContext)) {
            
        	IdentityStore identityStore = CDI.current().select(IdentityStore.class).get();
            
            Credential credential = httpMessageContext
        		.getAuthParameters()
                .getCredential();
            
            if (httpMessageContext.getAuthParameters().isNoPassword()) {
            	credential = new CallerOnlyCredential(credential.getCaller());
            }
            
            return httpMessageContext.notifyContainerAboutLogin(
                identityStore.validate(credential));
        }
		
		return httpMessageContext.doNothing();
	}
	
	private static boolean hasCredential(HttpMessageContext httpMessageContext) {
	    return 
            httpMessageContext.getAuthParameters().getCredential() != null;
	}
	
	// Workaround for CDI bug; at least in Weld 2.3.2 default methods
	// are not intercepted. Fixed in Weld 2.4.0 and 3.0.0, which we'll eventually target.
	// See https://issues.jboss.org/browse/WELD-2093
	@Override
	public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
		HttpAuthenticationMechanism.super.cleanSubject(request, response, httpMessageContext);
	}

}