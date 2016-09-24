package org.example.kickoff.business.email;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Startup
@Singleton
@ConcurrencyManagement(BEAN)
public class EmailLoader {

	private Map<String, String> emails;

	@PostConstruct
	public void init() {
		emails = loadPropertiesFromClasspath("META-INF/conf/emails");
	}

	@Produces
	@Named("emails")
	@Emails
	public Map<String, String> getEmails() {
		return emails;
	}

}