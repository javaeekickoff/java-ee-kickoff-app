package org.example.kickoff.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.example.kickoff.model.BaseEntity;
import org.example.kickoff.model.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserServiceTest {

	@Deployment
	public static Archive<?> createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		WebArchive archive = ShrinkWrap.create(WebArchive.class);

		archive.addClasses(UserService.class);
		archive.addClasses(InvalidCredentialsException.class);
		archive.addPackage(BaseEntity.class.getPackage());
		archive.addAsWebInfResource("test-web.xml", "web.xml");

		archive.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
		archive.addAsResource("META-INF/User.xml", "META-INF/User.xml");

		archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		archive.addAsLibraries(resolver.artifact("com.h2database:h2").resolveAsFiles());

		return archive;
	}

	@EJB
	private UserService userService;

	@Test
	public void testRegisterUser() {
		User user = new User();
		user.setUsername("name");
		user.setEmail("test@test.test");

		userService.registerUser(user, "TeSt");

		assertNotNull(user.getCredentials());
		assertNotNull(user.getId());

		User loggedInUser = userService.getUserByCredentials("name", "TeSt");

		assertNotNull(loggedInUser);
		assertEquals(user, loggedInUser);
	}
}
