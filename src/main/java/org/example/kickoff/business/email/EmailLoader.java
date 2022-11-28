package org.example.kickoff.business.email;

import static jakarta.ejb.ConcurrencyManagementType.BEAN;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;

import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

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