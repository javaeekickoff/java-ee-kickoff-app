package org.example.kickoff.plumbing.jaspic.request;

import static java.util.Arrays.copyOf;
import static java.util.Collections.emptyMap;
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
		requestData.setLocales(list(request.getLocales()));
		
		requestData.setMethod(request.getMethod()); // e.g. GET
		requestData.setRequestURL(request.getRequestURL().toString()); // e.g. http://localhost:8080/java-ee-kickoff-app/login.xhtml
		requestData.setQueryString(request.getQueryString()); // e.g. foo=bar&kaz=zak
	
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
			return emptyMap();
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
