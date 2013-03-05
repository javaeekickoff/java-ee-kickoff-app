package org.example.kickoff.servlet;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;


/**
 * This filter is used for initializing during the first request. In Java EE, some things just can't be done during
 * startup and lacking any kind of PostStartEvent, the only way to get those things initialized is using the hack
 * employed here.
 * <p>
 * What we do is calling a noop method (a method that has no effect) on an injected application scoped bean. This will
 * cause the PostConstruct method of that bean be executed on the very first request and on that request only. 
 * 
 * @author Arjan Tijms
 *
 */
@WebFilter(urlPatterns="/*", dispatcherTypes={REQUEST, FORWARD})
public class StartupFilter implements Filter {
	
	@Inject
	private StartupBean startupBean;
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		try { startupBean.noop(); } catch (Throwable e) {}
		
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig fConfig) throws ServletException {}
	public void destroy() {}
}
