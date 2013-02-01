package org.example.kickoff.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import javax.ejb.EJB;

import org.apache.catalina.util.Base64;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.example.kickoff.arquillian.ArquillianDBUnitTestBase;
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
import org.xml.sax.InputSource;

@RunWith(Arquillian.class)
public class UserServiceTest extends ArquillianDBUnitTestBase {

	@Deployment
	public static Archive<?> createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		WebArchive archive = ShrinkWrap.create(WebArchive.class);

		archive.addClass(ArquillianDBUnitTestBase.class);

		archive.addClasses(UserService.class);
		archive.addClasses(InvalidCredentialsException.class);
		archive.addPackage(BaseEntity.class.getPackage());
		archive.addAsWebInfResource("test-web.xml", "web.xml");

		archive.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
		archive.addAsResource("META-INF/User.xml", "META-INF/User.xml");

		archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		archive.addAsLibraries(resolver.artifact("com.h2database:h2").resolveAsFiles());
		archive.addAsLibraries(resolver.artifact("org.dbunit:dbunit").resolveAsFiles());

		archive.addAsResource("dbunit/user_service_test.xml");

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

		System.out.println(new String(Base64.encode(user.getCredentials().getPasswordHash()), StandardCharsets.UTF_8));
		System.out.println(new String(Base64.encode(user.getCredentials().getSalt()), StandardCharsets.UTF_8));
		User loggedInUser = userService.getUserByCredentials("name", "TeSt");

		assertNotNull(loggedInUser);
		assertEquals(user, loggedInUser);
	}

	@Test
	public void testGetUserByCredentials() {
		User user = userService.getUserByCredentials("Test1", "TeSt");
		assertNotNull(user);
		assertEquals("Test1", user.getUsername());

		try {
			userService.getUserByCredentials("Test1", "wrong_password");
			fail();
		}
		catch (Exception e) {
			// Exception should be thrown here
		}
	}

	@Override
	protected String getLookupName() {
		return "java:app/KickoffApp/kickoffUnitTestDS";
	}

	@Override
	protected IDataSet getTestDataSet() throws DataSetException {
		return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(this.getClass().getResourceAsStream("/dbunit/user_service_test.xml"))));
	}

}
