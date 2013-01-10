package org.example.kickoff.auth;

import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;

public class KickoffAuthConfigProvider implements AuthConfigProvider {

	private static final String CALLBACK_HANDLER_PROPERTY_NAME = "authconfigprovider.client.callbackhandler";

	private Map<String, String> providerProperties;

	public KickoffAuthConfigProvider() {
	}

	/**
	 * Constructor with signature and implementation that's required by API.
	 *
	 * @param properties
	 * @param factory
	 */
	public KickoffAuthConfigProvider(Map<String, String> properties, AuthConfigFactory factory) {
		this.providerProperties = properties;

		// API requires self registration if factory is provided. Not clear
		// where the "layer" (2nd parameter)
		// and especially "appContext" (3rd parameter) values have to come from
		// at this place.
		if (factory != null) {
			factory.registerConfigProvider(this, null, null, "Auto registration");
		}
	}

	/**
	 * The actual factory method that creates the factory used to eventually obtain the delegate for a SAM.
	 */
	@Override
	public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException, SecurityException {
		return new KickoffServerAuthConfig(layer, appContext, handler == null ? createDefaultCallbackHandler() : handler, providerProperties);
	}

	@Override
	public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException, SecurityException {
		return null;
	}

	@Override
	public void refresh() {
	}

	/**
	 * Creates a default callback handler via the system property "authconfigprovider.client.callbackhandler", as seemingly required by the API (API
	 * uses wording "may" create default handler).
	 *
	 * @return
	 * @throws AuthException
	 */
	private CallbackHandler createDefaultCallbackHandler() throws AuthException {
		String callBackClassName = System.getProperty(CALLBACK_HANDLER_PROPERTY_NAME);

		if (callBackClassName == null) {
			throw new AuthException("No default handler set via system property: " + CALLBACK_HANDLER_PROPERTY_NAME);
		}

		try {
			return (CallbackHandler) Thread.currentThread().getContextClassLoader().loadClass(callBackClassName).newInstance();
		}
		catch (Exception e) {
			throw new AuthException(e.getMessage());
		}
	}

}