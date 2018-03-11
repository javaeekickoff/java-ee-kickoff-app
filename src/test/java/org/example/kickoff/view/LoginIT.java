package org.example.kickoff.view;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

@RunWith(Arquillian.class)
public class LoginIT {

	@Deployment(testable=false)
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(MavenImporter.class)
			.loadPomFromFile("pom.xml")
			.importBuildOutput()
			.as(WebArchive.class)
			.addAsWebInfResource("test-web.xml", "web.xml")
			.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
	}

	@Drone
	private WebDriver browser;

	@ArquillianResource
	private URL baseURL;

	@FindBy(id = "loginForm:email")
	private WebElement email;

	@FindBy(id = "loginForm:password")
	private WebElement password;

	@FindBy(id = "loginForm:login")
	private WebElement login;

	@FindBy(id = "nav-user")
	private WebElement navUser;

	@FindBy(id = "logoutForm:logout")
	private WebElement logout;

	@Test
	public void testLoginAsAdmin() {
		browser.get(baseURL + "login");
		email.sendKeys("admin@kickoff.example.org");
		password.sendKeys("passw0rd");
		login.click();
		assertEquals(baseURL + "admin/users", browser.getCurrentUrl());
		logout();
	}

	@Test
	public void testLoginContinueAsUser() {
		browser.get(baseURL + "user/profile");
		assertEquals(baseURL + "login?continue=true", browser.getCurrentUrl());
		email.sendKeys("user@kickoff.example.org");
		password.sendKeys("passw0rd");
		login.click();
		assertEquals(baseURL + "user/profile", browser.getCurrentUrl());
		logout();
	}

	private void logout() {
		new Actions(browser).moveToElement(navUser).click(logout).perform();
		assertEquals(baseURL + "", browser.getCurrentUrl());
	}

}