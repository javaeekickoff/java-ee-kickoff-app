package org.example.kickoff.plumbing.jaspic.request;

import static java.util.Arrays.copyOf;
import static java.util.Collections.list;
import static org.omnifaces.util.Utils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestCopier {

	public static RequestData copy(HttpServletRequest request) {
		
		RequestData requestData = new RequestData();
		
		requestData.setCookies(copyCookies(request.getCookies()));
		requestData.setHeaders(copyHeaders(request));
		
		requestData.setParameters(copyParameters(request.getParameterMap()));
		
		requestData.setMethod(request.getMethod());
		requestData.setQueryString(request.getQueryString());
		requestData.setRequestURI(request.getRequestURI());
		
		requestData.setScheme(request.getScheme());
		requestData.setServerPort(request.getServerPort());
		requestData.setServletPath(request.getServletPath());
		requestData.setContextPath(request.getContextPath());		
		requestData.setPathInfo(request.getPathInfo());
		requestData.setRequestURL(request.getRequestURL().toString());
		requestData.setServerName(request.getServerName());
	
		return requestData;
	}
	
	
	private static Cookie[] copyCookies(Cookie[] cookies) {
		
		if (isEmpty(cookies)) {
			return cookies;
		}
		
		ArrayList<Cookie> copiedCookies = new ArrayList<>();
		for (Cookie cookie : cookies) {
			copiedCookies.add((Cookie)cookie.clone());
		}
		
		return copiedCookies.toArray(new Cookie[copiedCookies.size()]);
	}
	
	private static Map<String, List<String>> copyHeaders(HttpServletRequest request) {
	
		Map<String, List<String>> copiedHeaders = new HashMap<>();
		for (String headerName : list(request.getHeaderNames())) {
			copiedHeaders.put(headerName, list(request.getHeaders(headerName)));
		}
		
		return copiedHeaders;
	}
	
	private static Map<String, String[]> copyParameters(Map<String, String[]> parameters) {
		
		if (isEmptyMap(parameters)) {
			return parameters;
		}
		
		Map<String, String[]> copiedParameters = new HashMap<>();
		for (Map.Entry<String, String[]> parameter : parameters.entrySet()) {
			copiedParameters.put(parameter.getKey(), copyOf(parameter.getValue(), parameter.getValue().length));
		}
		
		return copiedParameters;
	}
	
	private static boolean isEmptyMap(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}
	
}
