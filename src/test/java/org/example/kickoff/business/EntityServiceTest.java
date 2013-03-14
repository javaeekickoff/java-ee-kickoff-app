package org.example.kickoff.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.ejb.EJB;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.example.kickoff.arquillian.ArquillianDBUnitTestBase;
import org.example.kickoff.business.test.NonDeletableTestEntity;
import org.example.kickoff.business.test.NonDeletableTestEntityService;
import org.example.kickoff.business.test.TestEntity;
import org.example.kickoff.business.test.TestEntityService;
import org.example.kickoff.model.BaseEntity;
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
public class EntityServiceTest extends ArquillianDBUnitTestBase {

	@Deployment
	public static Archive<?> createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		WebArchive archive = ShrinkWrap.create(WebArchive.class);

		archive.addClasses(ArquillianDBUnitTestBase.class, EntityServiceTest.class, EntityService.class, NonDeletable.class,
				NonDeletableEntityException.class);

		archive.addPackage(TestEntity.class.getPackage());
		archive.addAsWebInfResource("test-web.xml", "web.xml");

		archive.addPackage(BaseEntity.class.getPackage());

		archive.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
		archive.addAsResource("META-INF/User.xml", "META-INF/User.xml");

		archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		archive.addAsLibraries(resolver.artifact("com.h2database:h2").resolveAsFiles());
		archive.addAsLibraries(resolver.artifact("org.dbunit:dbunit").resolveAsFiles());

		archive.addAsResource("dbunit/entity_service_test.xml");

		return archive;
	}

	@EJB
	private TestEntityService testEntityService;

	@EJB
	private NonDeletableTestEntityService nonDeletableTestEntityService;

	@Override
	protected String getLookupName() {
		return "java:app/KickoffApp/kickoffUnitTestDS";
	}

	@Override
	protected IDataSet getTestDataSet() throws Exception {
		return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(this.getClass().getResourceAsStream("/dbunit/entity_service_test.xml"))));
	}

	@Test
	public void testGetById() {
		TestEntity testEntity = testEntityService.getById(1001L);
		assertNotNull(testEntity);
		assertEquals((Long) 1001L, testEntity.getId());

		assertNull(testEntityService.getById(2002L));
	}

	@Test
	public void testSave() {
		TestEntity testEntity = new TestEntity();
		testEntity.setName("Test entity 1");
		Long id = testEntityService.save(testEntity);

		TestEntity retrievedTestEntity = testEntityService.getById(id);
		assertEquals(testEntity, retrievedTestEntity);
	}

	@Test
	public void testUpdate() {
		TestEntity testEntity = testEntityService.getById(1001L);
		testEntity.setName("Changed entity");

		testEntityService.update(testEntity);

		assertEquals(testEntity.getName(), testEntityService.getById(1001L).getName());
	}

	@Test
	public void testDelete() {
		TestEntity testEntity = testEntityService.getById(1001L);
		testEntityService.delete(testEntity);

		assertNull(testEntityService.getById(1001L));

		NonDeletableTestEntity nonDeletableTestEntity = nonDeletableTestEntityService.getById(1L);

		try {
			nonDeletableTestEntityService.delete(nonDeletableTestEntity);
			fail();
		}
		catch (NonDeletableEntityException e) {
			// Expected exception
		}
	}

}
