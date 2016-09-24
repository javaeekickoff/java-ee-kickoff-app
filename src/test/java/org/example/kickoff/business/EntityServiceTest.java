package org.example.kickoff.business;

import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ejb.EJB;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.example.kickoff.arquillian.ArquillianDBUnitTestBase;
import org.example.kickoff.business.exception.BusinessException;
import org.example.kickoff.business.service.BaseEntityService;
import org.example.kickoff.business.test.TestEntity;
import org.example.kickoff.business.test.TestEntityService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.InputSource;

@RunWith(Arquillian.class)
public class EntityServiceTest extends ArquillianDBUnitTestBase {

	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive archive = ShrinkWrap.create(WebArchive.class);
		archive.addClass(ArquillianDBUnitTestBase.class);
		archive.addPackage(BaseEntityService.class.getPackage());
		archive.addPackage(BusinessException.class.getPackage());

		archive.addPackage(TestEntity.class.getPackage());
		archive.setWebXML("test-web.xml");
		archive.addAsWebInfResource(INSTANCE, "beans.xml");
		archive.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
		archive.addAsResource("META-INF/User.xml");
		archive.addAsResource("dbunit/EntityServiceTest.xml");
		archive.addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile());

		return archive;
	}

	@EJB
	private TestEntityService testEntityService;

	@Override
	protected String getLookupName() {
		return "java:app/kickoff/TestDataSource";
	}

	@Override
	protected IDataSet getTestDataSet() throws Exception {
		return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(this.getClass().getResourceAsStream("/dbunit/EntityServiceTest.xml"))));
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
	}

}
