package org.example.kickoff.business.email;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.example.kickoff.business.email.EmailTemplate.EmailTemplatePart;
import org.omnifaces.utils.text.NameBasedMessageFormat;

@Stateless
public class EmailTemplateService {

	@Inject
	@Emails
	private Map<String, String> emails;

	public String build(EmailTemplate templateEmail, EmailTemplatePart templatePart, Map<String, Object> messageParameters) {
		String pattern = templatePart.isHtml() 	? emails.get(templateEmail.getType() + "_" + templatePart.getKey() + "_HTML")
												: emails.get(templateEmail.getType() + "_" + templatePart.getKey() + "_text");

		// Defaults
		if (pattern == null) {
			pattern = templatePart.isHtml()	? emails.get("default_" + templatePart.getKey() + "_HTML")
											: emails.get("default_" + templatePart.getKey() + "_text");
		}

		if (pattern != null) {
			String content = NameBasedMessageFormat.format(pattern, messageParameters).trim();

			if (templatePart.isHtml()) {
				content = content.replaceAll("(\r\n|\n){2,}", "<br/><br/>");
			}

			return content;
		}

		return null;
	}

	public Map<String, Object> addUserParameters(String prefix, EmailUser user, Map<String, Object> messageParameters) {
		messageParameters.putIfAbsent(prefix + ".id", user.getId());
		messageParameters.putIfAbsent(prefix + ".email", user.getEmail());
		messageParameters.putIfAbsent(prefix + ".fullName", user.getFullName());

		String linkPattern = String.format("<span class=\"profile\">%s</span>", String.format("{%s.fullName}", prefix));
		messageParameters.putIfAbsent(prefix, NameBasedMessageFormat.format(linkPattern, messageParameters));

		return messageParameters;
	}

}