package org.example.kickoff.business;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.example.kickoff.business.service.LoginTokenService;

@Singleton
public class BackgroundJobs {

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private LoginTokenService loginTokenService;

	@Schedule(dayOfWeek = "*", hour = "*", minute = "0", second = "0", persistent = false)
	public void hourly() {
		loginTokenService.removeExpired();
	}

}