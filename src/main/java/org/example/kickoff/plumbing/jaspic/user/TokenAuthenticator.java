package org.example.kickoff.plumbing.jaspic.user;

public interface TokenAuthenticator extends Authenticator {
	
	boolean authenticate(String token);
	String generateLoginToken();

}
