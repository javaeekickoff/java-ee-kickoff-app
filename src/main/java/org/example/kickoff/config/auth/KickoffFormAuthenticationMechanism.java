package org.example.kickoff.config.auth;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
 */
@AutoApplySession // For "Is user already logged-in?"
@RememberMe(
	cookieMaxAgeSeconds = 60 * 60 * 24 * 14, // 14 days
	isRememberMeExpression = "httpMessageContext.authParameters.rememberMe" // Workaround for Soteria bug; authParam.rememberMe is ignored when @RememberMe annotation is set.
)
@LoginToContinue(
	loginPage = "/login?continue=true",
	errorPage = "",
	useForwardToLogin = false
)
@ApplicationScoped
public class KickoffFormAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStore identityStore;

	@Override
	public AuthStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthException {
		Credential credential = context.getAuthParameters().getCredential();

		if (credential != null) {
            if (context.getAuthParameters().isNoPassword()) {
            	credential = new CallerOnlyCredential(credential.getCaller());
            }

            return context.notifyContainerAboutLogin(identityStore.validate(credential));
        }

		return context.doNothing();
	}

    // Workaround for CDI bug; at least in Weld 2.3.2 default methods are not intercepted.
	// Fixed in Weld 2.4.0 and 3.0.0, which we'll eventually target.
	// See https://issues.jboss.org/browse/WELD-2093
	@Override
	public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {
		HttpAuthenticationMechanism.super.cleanSubject(request, response, context);
	}

}