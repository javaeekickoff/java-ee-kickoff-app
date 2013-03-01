package org.example.kickoff.validator;

import static org.omnifaces.util.Messages.addGlobalError;

import org.example.kickoff.model.User;

public class Demo {

	public static boolean canUpdate(User user) {
		if (user.getId().equals(1l)) {
			addGlobalError("Sorry, this user can''t be edited for the demo");
			return false;
		}
		
		return true;
	}
	
}
