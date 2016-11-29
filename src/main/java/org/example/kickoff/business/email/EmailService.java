package org.example.kickoff.business.email;

import static org.example.kickoff.business.email.EmailTemplate.EmailTemplatePart.BODY_TITLE;
import static org.example.kickoff.business.email.EmailTemplate.EmailTemplatePart.SUBJECT_CONTENT;
import static org.omnifaces.utils.Lang.isEmpty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.example.kickoff.business.email.EmailTemplate.EmailTemplatePart;
import org.example.kickoff.business.exception.SystemException;
import org.omnifaces.cdi.settings.ApplicationSetting;

public abstract class EmailService  {

	private static final String LOG_MAIL_FAIL = "Failed to send mail [type=%s, to=%s, replyTo=%s, subject=%s]";
	private static final List<String> ALLOWED_FROM_DOMAINS = Arrays.asList("kickoff.example.org");

	private EmailUser defaultEmailUser;

	@Inject @ApplicationSetting
	private String fromEmail;

	@Inject @ApplicationSetting
	private Boolean disableEmailService;

	@Inject
	private EmailTemplateService emailTemplateService;

	@PostConstruct
	public void init() {
		defaultEmailUser = new EmailUser(fromEmail, "The Java EE Kickoff Team");
	}

	public void sendTemplate(EmailTemplate templateEmail) {
		sendTemplate(templateEmail, new HashMap<>());
	}

	public void sendTemplate(EmailTemplate templateEmail, Map<String, Object> messageParameters) {
		if (disableEmailService) {
			return;
		}

		if (templateEmail.getFromUser() == null) {
			templateEmail.setFromUser(defaultEmailUser);
		}
		else { // Prevent sending email from other domains.
			EmailUser fromUser = templateEmail.getFromUser();
			String email = fromUser.getEmail();

			if (email != null && email.contains("@") && !ALLOWED_FROM_DOMAINS.contains(email.substring(email.indexOf("@") + 1))) {
				templateEmail.setFromUser(new EmailUser(defaultEmailUser.getEmail(), fromUser.getFullName() + " via Java EE Kickoff App"));
				templateEmail.setReplyTo(fromUser.getEmail());
			}
		}

		if (templateEmail.getToUser() == null) {
			templateEmail.setToUser(defaultEmailUser);
		}

		emailTemplateService.addUserParameters("toUser", templateEmail.getToUser(), messageParameters);
		emailTemplateService.addUserParameters("fromUser", templateEmail.getFromUser(), messageParameters);

		try {
			Map<String, String> templateContent = buildTemplateContent(templateEmail, messageParameters);
			sendTemplateMessage(templateEmail, messageParameters, templateContent);
		}
		catch (Exception e) {
			String errorMessage = String.format(LOG_MAIL_FAIL, templateEmail.getType(), templateEmail.getToUser().getEmail(), templateEmail.getReplyTo(), templateEmail.getTemplateParts().get(SUBJECT_CONTENT));
			throw new SystemException(errorMessage, e);
		}
	}

	public abstract void sendTemplateMessage(EmailTemplate templateEmail, Map<String, Object> messageParameters, Map<String, String> templateContent);

	public void sendPlainText(EmailUser to, String subject, String body) {
		sendPlainText(to, defaultEmailUser, subject, body, null);
	}

	public void sendPlainText(EmailUser to, EmailUser from, String subject, String body, String replyTo) {
		if (disableEmailService) {
			return;
		}

		try {
			sendPlainTextMessage(to, from, subject, body, replyTo);
		}
		catch (Exception e) {
			String errorMessage = String.format(LOG_MAIL_FAIL, to, replyTo, subject, body);
			throw new SystemException(errorMessage, e);
		}
	}

	public abstract void sendPlainTextMessage(EmailUser to, EmailUser from, String subject, String body, String replyTo);

	private Map<String, String> buildTemplateContent(EmailTemplate templateEmail, Map<String, Object> messageParameters) {
		Map<EmailTemplatePart, String> templateParts = templateEmail.getTemplateParts();
		Arrays.stream(EmailTemplatePart.values())
			.filter(part -> !templateParts.containsKey(part.getKey()))
			.forEach(part -> templateParts.putIfAbsent(part, emailTemplateService.build(templateEmail, part, messageParameters)));

		if (isEmpty(templateParts.get(BODY_TITLE))) {
			String subjectContent = templateParts.get(SUBJECT_CONTENT);
			templateParts.put(BODY_TITLE, subjectContent);
		}

		Map<String, String> templateContent = new HashMap<>();
		templateEmail.getTemplateParts().keySet().stream()
			.forEach(key -> templateContent.put(key.getKey(), templateEmail.getTemplateParts().get(key)));

		return templateContent;
	}

}