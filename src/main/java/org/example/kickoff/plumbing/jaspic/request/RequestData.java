package org.example.kickoff.plumbing.jaspic.request;

import static org.omnifaces.util.Utils.isEmpty;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestData {

	private Cookie[] cookies;
	private Map<String, List<String>> headers;
	private List<Locale> locales;
	private Map<String, String[]> parameters;

	private String method;
	private String requestURL;
	private String queryString;
	

	public Cookie[] getCookies() {
		return cookies;
	}

	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public void setLocales(List<Locale> locales) {
		this.locales = locales;
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public String getFullRequestURL() {
		return buildFullRequestURL(requestURL, queryString);
	}
	
	public boolean matchesRequest(HttpServletRequest request) {
		// (or use requestURI instead of requestURL?)
		return getFullRequestURL().equals(buildFullRequestURL(request.getRequestURL().toString(), request.getQueryString()));
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", method, getFullRequestURL());
	}
	
	private String buildFullRequestURL(String requestURL, String queryString) {
		return requestURL + (isEmpty(queryString) ? "" : "?" + queryString);
	}

}
