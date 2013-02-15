package org.example.kickoff.servlet;

import static org.example.kickoff.model.Group.ADMINISTRATORS;
import static org.example.kickoff.model.Group.USERS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.kickoff.business.InvalidCredentialsException;
import org.example.kickoff.business.UserService;
import org.example.kickoff.model.Group;
import org.example.kickoff.model.User;

@WebServlet(urlPatterns = "/public", loadOnStartup = 1)
public class PublicServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
	@EJB
	private UserService userService;
	
	@Override
	public void init() throws ServletException {
	 	try {
			userService.getUserByCredentials("foo@bar.com", "foo");
		} catch (InvalidCredentialsException e) {
			User user = new User();
			user.setEmail("foo@bar.com");
			
			List<Group> group = new ArrayList<>();
			group.add(ADMINISTRATORS);
			group.add(USERS);

			user.setGroups(group);
			
			userService.registerUser(user, "foo");
		}
    	
		try {
			userService.getUserByCredentials("foo1@bar.com", "foo");
		} catch (InvalidCredentialsException e) {
			User user = new User();
			user.setEmail("foo1@bar.com");
			
			List<Group> group = new ArrayList<>();
			group.add(ADMINISTRATORS);
			group.add(USERS);

			user.setGroups(group);
			
			userService.registerUser(user, "foo");
		}
	}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.getWriter().write(
            "public"
        );
        
   
    }

}
