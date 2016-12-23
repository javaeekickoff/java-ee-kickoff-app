package org.example.kickoff.view.servlet;

import static org.omnifaces.util.Servlets.getRequestParameterMap;
import static org.omnifaces.util.Servlets.getRequestURI;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is triggered by window.onerror in onload.js.
 */
@WebServlet("/script-error")
public class ScriptErrorLogger extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.warning("Script error: " + getRequestURI(request) + ", " + getRequestParameterMap(request));
	}

}