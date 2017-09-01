package org.example.kickoff.config.auth;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.message.AuthException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Authentication mechanism that authenticates according to the Servlet spec defined FORM
 * authentication mechanism. See Servlet spec for further details.
 *
 * @author Arjan Tijms
 */
@AutoApplySession // For "Is user already logged-in?"
@RememberMe(isRememberMeExpression = "httpMessageContext.authParameters.rememberMe", cookieMaxAgeSeconds = 60 * 60 * 24 * 14) // 14 days
@LoginToContinue(loginPage = "/login?continue=true", errorPage = "", useForwardToLogin = false)
@ApplicationScoped
public class KickoffFormAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStore identityStore;

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthException {
		Credential credential = context.getAuthParameters().getCredential();

		if (credential != null) {
            return context.notifyContainerAboutLogin(identityStore.validate(credential));
        }
		else {
			return context.doNothing();
		}
	}

    // Workaround for CDI bug; default methods are not intercepted.
	// Fixed in Weld 2.4.0 and 3.0.0, which we'll eventually target.
	// See https://issues.jboss.org/browse/WELD-2093
	@Override
	public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
		HttpAuthenticationMechanism.super.cleanSubject(request, response, httpMessageContext);
	}

}