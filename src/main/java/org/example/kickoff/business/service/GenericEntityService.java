package org.example.kickoff.business.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GenericEntityService extends org.omnifaces.persistence.service.GenericEntityService {

	@PersistenceContext
	private EntityManager entityManager;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@PostConstruct
	public void init() {
		setEntityManager(entityManager);
		setEntityManagerFactory(entityManagerFactory);
	}

}