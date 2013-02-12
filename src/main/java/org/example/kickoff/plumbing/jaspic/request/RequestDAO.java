package org.example.kickoff.plumbing.jaspic.request;

import static org.example.kickoff.plumbing.jaspic.request.RequestCopier.copy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestDAO {
	
	private static final String ORIGINAL_REQUEST_DATA_SESSION_NAME = "org.example.kickoff.jaspic.original.request";
	
	public void save(HttpServletRequest request) {
		request.getSession().setAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME, copy(request));
	}
	
	public RequestData get(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		
		return (RequestData) session.getAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME);
	}
	
	public void remove(HttpServletRequest request) {
		request.getSession().removeAttribute(ORIGINAL_REQUEST_DATA_SESSION_NAME);
	}

}
