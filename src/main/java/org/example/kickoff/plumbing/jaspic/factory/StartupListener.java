package org.example.kickoff.plumbing.jaspic.factory;

import javax.ejb.EJB;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.User;
import org.example.kickoff.plumbing.jaspic.KickoffServerAuthModule;

/**
 * 
 * @author arjan
 *
 */
@WebListener
public class StartupListener implements ServletContextListener {

	@EJB
	private UserService userService;
	
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		// Register the factory-factory-factory for the SAM
		AuthConfigFactory.getFactory().registerConfigProvider(
			new KickoffAuthConfigProvider(), 
			"HttpServlet", null, "Kickoff app authentication config provider"
		);
		
		// Register the SAM separately as a filter
		sce.getServletContext().addFilter(
			KickoffServerAuthModule.class.getName(), 
			KickoffServerAuthModule.class
		).addMappingForUrlPatterns(null, false, "/*");
		
		try {
			userService.getUserByCredentials("foo@bar.com", "foo");
		} catch (InvalidCredentialsException e) {
			User user = new User();
			user.setEmail("foo@bar.com");
			
			userService.registerUser(user, "foo");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}