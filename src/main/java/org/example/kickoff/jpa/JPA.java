package org.example.kickoff.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
}
