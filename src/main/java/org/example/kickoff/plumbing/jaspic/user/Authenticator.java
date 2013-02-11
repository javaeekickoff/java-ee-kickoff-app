package org.example.kickoff.plumbing.jaspic.user;

import java.io.Serializable;
import java.util.List;

public interface Authenticator extends Serializable {

	String getUserName();
	List<String> getApplicationRoles();
	
}
