package org.example.kickoff.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.ejb.EJB;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.example.kickoff.arquillian.ArquillianDBUnitTestBase;
import org.example.kickoff.business.exception.CredentialsException;
import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.InputSource;

@RunWith(Arquillian.class)
public class UserServiceTest extends ArquillianDBUnitTestBase {

	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(MavenImporter.class)
			.loadPomFromFile("pom.xml")
			.importBuildOutput()
			.as(WebArchive.class)
			.addAsWebInfResource("test-web.xml", "web.xml")
			.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
			.addClass(ArquillianDBUnitTestBase.class)
			.addAsResource("dbunit/UserServiceTest.xml");
	}

	@EJB
	private UserService userService;

	@Override
	protected String getLookupName() {
		return "java:app/kickoff/TestDataSource";
	}

	@Override
	protected IDataSet getTestDataSet() throws DataSetException {
		return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(this.getClass().getResourceAsStream("/dbunit/UserServiceTest.xml"))));
	}

	@Test
	public void testRegisterUser() {
		User user = new User();
		user.setEmail("test2@test.test");
		user.setFirstName("test2");
		user.setLastName("test");

		userService.register(user, "TeSt");

		assertNotNull(user.getCredentials());
		assertNotNull(user.getId());

		User loggedInUser = userService.getByEmailAndPassword("test2@test.test", "TeSt");

		assertNotNull(loggedInUser);
		assertEquals(user, loggedInUser);
	}

	@Test
	public void testGetUserByCredentials() {
		User user = userService.getByEmailAndPassword("test@test.test", "TeSt");
		assertNotNull(user);
		assertEquals("test@test.test", user.getEmail());

		try {
			userService.getByEmailAndPassword("Test1", "wrong_password");
			fail();
		}
		catch (CredentialsException e) {
			// Exception should be thrown here
		}

		try {
			userService.getByEmailAndPassword("non_existant_username", "password");
			fail();
		}
		catch (CredentialsException e) {
			// Exception should be thrown here
		}
	}

	@Test
	public void testUpdatePassword() {
		User user = userService.getByEmailAndPassword("test@test.test", "TeSt");

		userService.updatePassword(user, "TeSt2");

		try {
			userService.getByEmailAndPassword("test@test.test", "TeSt");
			fail();
		}
		catch (Exception e) {
			// Exception should be thrown here
		}

		User loggedInUser = userService.getByEmailAndPassword("test@test.test", "TeSt2");
		assertNotNull(loggedInUser);
		assertEquals(user, loggedInUser);
	}

}