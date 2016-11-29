package org.example.kickoff.business.email;

import java.util.Map;

import javax.ejb.Stateless;

@Stateless
public class KickoffEmailService extends EmailService {

	@Override
	public void sendTemplateMessage(EmailTemplate templateEmail, Map<String, Object> messageParameters, Map<String, String> templateContent) {
		// TODO: implement.
		System.out.println("KickoffEmailService.sendTemplateMessage()");
		System.out.println("Call to action URL: " + templateEmail.getCallToActionURL());
		System.out.println("Message parameters: " + messageParameters);
		System.out.println("Template content: " + templateContent);
	}

	@Override
	public void sendPlainTextMessage(EmailUser to, EmailUser from, String subject, String body, String replyTo) {
		// TODO: implement.
		System.out.println("KickoffEmailService.sendPlainTextMessage()");
		System.out.println("To: " + to);
		System.out.println("From: " + from);
		System.out.println("Subject: " + subject);
		System.out.println("Body: " + body);
		System.out.println("Reply to: " + replyTo);
	}

}