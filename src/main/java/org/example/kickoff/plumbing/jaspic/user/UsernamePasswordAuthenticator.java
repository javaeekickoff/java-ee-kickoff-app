package org.example.kickoff.plumbing.jaspic.user;

public interface UsernamePasswordAuthenticator extends Authenticator {

	boolean authenticate(String name, String password);
		
}
