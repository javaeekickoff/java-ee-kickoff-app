package org.example.kickoff.jpa;

import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

// TODO: replace by OmniPersistence dependency
public final class JPA {

	private JPA() {
	}

	public static <T> T getOptionalSingleResult(TypedQuery<T> query) {
		try {
			return query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	public static <T> T getOptionalSingleResult(Query query, Class<T> clazz) {
		try {
			return clazz.cast(query.getSingleResult());
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	public static <T> Optional<T> getOptional(TypedQuery<T> query) {
		try {
			query.setMaxResults(1);
			return Optional.of(query.getSingleResult());
		}
		catch (NoResultException e) {
			return Optional.empty();
		}
	}
}
