package org.example.kickoff.plumbing.jaspic;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 
 * @author arjan
 *
 */
@WebListener
public class StartupListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		AuthConfigFactory factory = AuthConfigFactory.getFactory();
		factory.registerConfigProvider(new KickoffAuthConfigProvider(), "HttpServlet", null, "Kickoff app authentication config provider");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}