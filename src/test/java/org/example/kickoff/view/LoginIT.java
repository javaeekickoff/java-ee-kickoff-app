package org.example.kickoff.view;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

@RunWith(Arquillian.class)
public class LoginIT {

	@Deployment(testable=false)
	public static WebArchive createDeployment() {
	    WebArchive webArchive = ShrinkWrap.create(MavenImporter.class)
			.loadPomFromFile("pom.xml")
			.importBuildOutput()
			.as(WebArchive.class)
			.addAsWebInfResource("test-web.xml", "web.xml")
			.addAsResource("test-persistence.xml", "META-INF/persistence.xml");

	    System.out.println(webArchive.toString(true));

	    return webArchive;
	}

	private WebDriver browser = new ChromeDriver();

	@ArquillianResource
	private URL baseURL;

	@Test
	public void testLoginAsAdmin() {
		browser.get(baseURL + "login");

		login();
		assertEquals(baseURL + "admin/users", browser.getCurrentUrl());

		logout();
	}

	@Test
	public void testLoginContinueAsUser() {
		browser.get(baseURL + "user/profile");
		assertEquals(baseURL + "login?continue=true", browser.getCurrentUrl());

		login();
		assertEquals(baseURL + "user/profile", browser.getCurrentUrl());

		logout();
	}

	private void login() {
	    findById("loginForm:email").sendKeys("admin@kickoff.example.org");
        findById("loginForm:password").sendKeys("passw0rd");
        findById("loginForm:login").click();
	}

	private void logout() {
		new Actions(browser).moveToElement(findById("nav-user")).click(findById("logoutForm:logout")).perform();
		assertEquals(baseURL + "", browser.getCurrentUrl());
	}

	private WebElement findById(String id) {
	    return browser.findElement(By.id(id));
	}

}