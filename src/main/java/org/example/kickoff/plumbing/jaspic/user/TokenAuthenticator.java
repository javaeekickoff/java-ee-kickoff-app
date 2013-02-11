package org.example.kickoff.plumbing.jaspic.user;

public interface TokenAuthenticator {
	
	boolean authenticate(String token);

}
