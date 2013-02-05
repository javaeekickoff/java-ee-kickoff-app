package org.example.kickoff.plumbing.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

public class Beans {
	
	public static <T> T getReference(Class<T> beanClass) {
		return getReference(beanClass, getBeanManager());
	}

	@SuppressWarnings("unchecked")
	public static <T> T getReference(Class<T> beanClass, BeanManager beanManager) {

		Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(beanClass));

		return (T) beanManager.getReference(bean, beanClass, beanManager.createCreationalContext(bean));
	}

	public static <T> T getInstance(final Class<T> type, final Class<? extends Annotation> scope) {
		return getInstance(type, scope, getBeanManager());
	}

	public static <T> T getInstance(final Class<T> type, final Class<? extends Annotation> scope, final BeanManager beanManager) {
		
		@SuppressWarnings("unchecked")
		Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));

		return beanManager.getContext(scope).get(bean, beanManager.createCreationalContext(bean));
	}
	
	public static BeanManager getBeanManager() {
		try {
			InitialContext context = new InitialContext();
			return (BeanManager) context.lookup("java:comp/BeanManager");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
