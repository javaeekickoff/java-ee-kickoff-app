package org.example.kickoff.business;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.model.BaseEntity;

public abstract class EntityService<I extends Number, E extends BaseEntity<I>> {

	@PersistenceContext
	private EntityManager entityManager;

	private Class<E> entityClass;

	@SuppressWarnings("unchecked")
	public EntityService() {
		Type type = getClass().getGenericSuperclass();
		while (!(type instanceof ParameterizedType) || !EntityService.class.equals(((ParameterizedType) type).getRawType())) {
			if (type instanceof ParameterizedType) {
				type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
			}
			else {
				type = ((Class<?>) type).getGenericSuperclass();
			}
		}

		entityClass = (Class<E>) ((ParameterizedType) type).getActualTypeArguments()[1];
	}

	public E getById(I id) {
		return entityManager.find(entityClass, id);
	}

	public I save(E entity) {
		entityManager.persist(entity);

		return entity.getId();
	}

	public void update(E entity) {
		entityManager.merge(entity);
	}

	public void delete(E entity) {
		if (entity.getClass().isAnnotationPresent(NonDeletable.class)) {
			throw new NonDeletableEntityException();
		}

		if (entityManager.contains(entity)) {
			entityManager.remove(entity);
		}
		else {
			entityManager.remove(getById(entity.getId()));
		}
	}

}
