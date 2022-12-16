package org.example.kickoff.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.example.kickoff.arquillian.ArquillianDBUnitTestBase;
import org.example.kickoff.business.exception.CredentialsException;
import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.InputSource;

import jakarta.ejb.EJB;

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
			.addClasses(ArquillianDBUnitTestBase.class)
			.addAsResource("dbunit/UserServiceTest.xml");
	}

	@EJB
	private PersonService personService;

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
		Person person = new Person();
		person.setEmail("test2@test.test");
		person.setFirstName("test2");
		person.setLastName("test");

		personService.register(person, "TeSt");

		assertNotNull(person.getCredentials());
		assertNotNull(person.getId());

		Person loggedInUser = personService.getByEmailAndPassword("test2@test.test", "TeSt");

		assertNotNull(loggedInUser);
		assertEquals(person, loggedInUser);
	}

	@Test
	public void testGetUserByCredentials() {
		Person person = personService.getByEmailAndPassword("test@test.test", "TeSt");
		assertNotNull(person);
		assertEquals("test@test.test", person.getEmail());

		try {
			personService.getByEmailAndPassword("Test1", "wrong_password");
			fail();
		}
		catch (CredentialsException e) {
			// Exception should be thrown here
		}

		try {
			personService.getByEmailAndPassword("non_existant_username", "password");
			fail();
		}
		catch (CredentialsException e) {
			// Exception should be thrown here
		}
	}

	@Test
	public void testUpdatePassword() {
		Person person = personService.getByEmailAndPassword("test@test.test", "TeSt");

		personService.updatePassword(person, "TeSt2");

		try {
			personService.getByEmailAndPassword("test@test.test", "TeSt");
			fail();
		}
		catch (Exception e) {
			// Exception should be thrown here
		}

		Person loggedInUser = personService.getByEmailAndPassword("test@test.test", "TeSt2");
		assertNotNull(loggedInUser);
		assertEquals(person, loggedInUser);
	}

}