package org.example.kickoff.view.servlet;

import static org.omnifaces.util.Servlets.getRequestParameterMap;
import static org.omnifaces.util.Servlets.getRequestURI;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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