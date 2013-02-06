package org.example.kickoff.plumbing.cdi;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;

@Stateless
public class BeanResolver {

	@Resource(lookup="java:comp/BeanManager")
	private BeanManager beanManager;
	
	public <T> T getInstance(final Class<T> type, final Class<? extends Annotation> scope) {		
		return Beans.getInstance(type, scope, beanManager);
	}
	
	public <T> T getReference(Class<T> beanClass) {
		return Beans.getReference(beanClass, beanManager);
	}
	
}
