package org.example.kickoff.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that's publicly available. Only used for testing.
 * 
 * @author Arjan Tijms
 *
 */
@WebServlet(urlPatterns = "/public")
public class PublicServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.getWriter().write(
            "public"
        );
        
   
    }

}
