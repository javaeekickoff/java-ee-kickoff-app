package org.example.kickoff.business.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.example.kickoff.business.exception.NonDeletableEntityException;
import org.omnifaces.persistence.model.BaseEntity;
import org.omnifaces.persistence.model.NonDeletable;
import org.omnifaces.persistence.model.dto.SortFilterPage;
import org.omnifaces.utils.collection.PartialResultList;

public abstract class BaseEntityService<I extends Number, E extends BaseEntity<I>> {

	@PersistenceContext
	private EntityManager entityManager;

	private final Class<E> entityClass;

	@Inject
	private GenericEntityService genericEntityService;

	public BaseEntityService() {
		entityClass = detectEntityClass();
	}

	@SuppressWarnings("unchecked")
	private Class<E> detectEntityClass() {
		Type type = getClass().getGenericSuperclass();
		Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();

		while (!(type instanceof ParameterizedType) ||
		       !BaseEntityService.class.equals(((ParameterizedType) type).getRawType())) {
			if (type instanceof ParameterizedType) {
				Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
				Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

				for (int i = 0; i < typeParameters.length; i++) {
					if (typeArguments[i] instanceof TypeVariable) {
						typeMapping.put(typeParameters[i], typeMapping.get(typeArguments[i]));
					}
					else {
						typeMapping.put(typeParameters[i], typeArguments[i]);
					}
				}

				type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
			}
			else {
				type = ((Class<?>) type).getGenericSuperclass();
			}
		}

		Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[1];

		if (typeArgument instanceof TypeVariable) {
			typeArgument = typeMapping.get(typeArgument);
		}

		return (Class<E>) typeArgument;
	}

	public E get(E entity) {
		return getById(entity.getId());
	}

	public E getById(I id) {
		return entityManager.find(entityClass, id);
	}

	public PartialResultList<E> getByPage(SortFilterPage sortFilterPage, boolean getCount) {
		return genericEntityService.getAllPagedAndSorted(entityClass,
			(builder, query, tp) -> query.from(entityClass),
			new HashMap<>(),
			sortFilterPage,
			getCount
		);
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

		entityManager.remove(entityManager.contains(entity) ? entity : get(entity));
	}

}