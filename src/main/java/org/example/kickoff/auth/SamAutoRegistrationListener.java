package org.example.kickoff.auth;

import static org.omnifaces.security.jaspic.config.ControlFlag.REQUIRED;
import static org.omnifaces.security.jaspic.core.Jaspic.getAppContextID;
import static org.omnifaces.security.jaspic.wrappers.SaveAndRedirectWrapper.PUBLIC_REDIRECT_URL;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.message.config.AuthConfigFactory;
//import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.omnifaces.security.jaspic.authmodules.OmniServerAuthModule;
import org.omnifaces.security.jaspic.config.AuthStacks;
import org.omnifaces.security.jaspic.config.AuthStacksBuilder;
import org.omnifaces.security.jaspic.factory.OmniAuthConfigProvider;
//import org.omnifaces.security.jaspic.filters.OmniServerAuthFilter;

/**
 * This listener automatically registers the SAM when the web application is starting.
 *
 *
 * @author Arjan Tijms
 *
 */
@WebListener
public class SamAutoRegistrationListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Map<String, String> socialOptions = new HashMap<>();
		socialOptions.put(PUBLIC_REDIRECT_URL, "/login");

		AuthStacks stacks = new AuthStacksBuilder()
		 	.stack()
		 		.name("jsf-form")
		 		.setDefault()
		 		.module()
					.serverAuthModule(new OmniServerAuthModule())
					.controlFlag(REQUIRED)
					.option(PUBLIC_REDIRECT_URL, "/login")
					.add()
				.add()
			.build();

		// Register the factory-factory-factory for the SAM
		AuthConfigFactory.getFactory().registerConfigProvider(
			new OmniAuthConfigProvider(stacks),
			"HttpServlet", getAppContextID(sce.getServletContext()), "OmniSecurity authentication config provider"
		);

		
		// Filter registration removed. Assume CDI is available.
		// NOTE: this currently breaks login-to-continue, which needs to be fixed.
		
//		// Register the SAM separately as a filter. This is hack to let JASPIC do its thing within
//		// a web component context, so that CDI, EJB etc are available, and to implement
		// login-to-continue

//		FilterRegistration.Dynamic registration = sce.getServletContext().addFilter(
//			OmniServerAuthFilter.class.getName(),
//			OmniServerAuthFilter.class
//		);
//
//		registration.addMappingForUrlPatterns(null, false, "/*");
//		registration.setAsyncSupported(true);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// NOOP.
	}
}