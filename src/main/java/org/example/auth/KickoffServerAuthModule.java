package org.example.auth;

import static javax.security.auth.message.AuthStatus.SEND_SUCCESS;
import static javax.security.auth.message.AuthStatus.SUCCESS;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The actual Server Authentication Module AKA SAM.
 *
 */
public class KickoffServerAuthModule implements ServerAuthModule {

	private CallbackHandler handler;
	private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

	@Override
	public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
			@SuppressWarnings("rawtypes") Map options) throws AuthException {
		this.handler = handler;
	}

	@Override
	public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

		// Normally we would check here for authentication credentials being
		// present and perform actual authentication, or in absence of those
		// ask the user in some way to authenticate.

		// Here we just create the user and associated roles directly.

		// Create a handler (kind of directive) to add the caller principal (AKA
		// user principal) "test" (=basically user name, or user id)
		// This will be the name of the principal returned by e.g.
		// HttpServletRequest#getUserPrincipal
		CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, "test");

		// Create a handler to add the group (AKA role) "architect"
		// This is what e.g. HttpServletRequest#isUserInRole and @RolesAllowed
		// test for
		GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, new String[] { "architect" });

		// Execute the handlers we created above. This will typically add the
		// "test" principal and the "architect"
		// role in an application server specific way to the JAAS Subject.
		try {
			handler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });
		}
		catch (IOException | UnsupportedCallbackException e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

	/**
	 * A compliant implementation should return HttpServletRequest and HttpServletResponse, so the delegation class {@link ServerAuthContext} can
	 * choose the right SAM to delegate to. In this example there is only one SAM and thus the return value actually doesn't matter here.
	 */
	@Override
	public Class<?>[] getSupportedMessageTypes() {
		return supportedMessageTypes;
	}

	/**
	 * WebLogic 12c calls this before Servlet is called, Geronimo v3 after, JBoss EAP 6 and GlassFish 3.1.2.2 don't call this at all. WebLogic
	 * (seemingly) only continues if SEND_SUCCESS is returned, Geronimo completely ignores return value.
	 */
	@Override
	public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
		return SEND_SUCCESS;
	}

	@Override
	public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

	}
}