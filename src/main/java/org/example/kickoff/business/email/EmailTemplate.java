package org.example.kickoff.business.email;

import java.util.HashMap;
import java.util.Map;

public class EmailTemplate {

	public enum EmailTemplatePart {
		SUBJECT_CONTENT("subject_content", false),
		SALUTATION_CONTENT("salutation_content", false),
		BODY_TITLE("body_title", false),
		BODY_CONTENT("body_content", true),
		BODY2_TITLE("body2_title", true),
		BODY2_CONTENT("body2_content", true),
		OBJECT_CONTENT("object_content", true),
		CALL_TO_ACTION1_LABEL("call_to_action1_label", false),
		CALL_TO_ACTION2_LABEL("call_to_action2_label", false);

		private final String key;
		private final boolean html;

		EmailTemplatePart(String key, boolean html) {
			this.key = key;
			this.html = html;
		}

		public String getKey() {
			return key;
		}

		public boolean isHtml() {
			return html;
		}
	}

	// TODO convert to enum
	private String type;

	private EmailUser toUser;
	private EmailUser fromUser;
	private String replyTo;
	private boolean sendBccToFromUser = false;

	private Map<EmailTemplatePart, String> templateParts = new HashMap<>();

	private String callToAction1URL;
	private String callToAction2URL;

	public EmailTemplate() {
	}

	public EmailTemplate(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public EmailTemplate setType(String type) {
		this.type = type;
		return this;
	}

	public EmailUser getToUser() {
		return toUser;
	}

	public EmailTemplate setToUser(EmailUser toUser) {
		this.toUser = toUser;
		return this;
	}

	public EmailUser getFromUser() {
		return fromUser;
	}

	public EmailTemplate setFromUser(EmailUser fromUser) {
		this.fromUser = fromUser;
		return this;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public EmailTemplate setReplyTo(String replyTo) {
		this.replyTo = replyTo;
		return this;
	}

	public boolean isSendBccToFromUser() {
		return sendBccToFromUser;
	}

	public EmailTemplate setSendBccToFromUser(boolean sendBccToFromUser) {
		this.sendBccToFromUser = sendBccToFromUser;
		return this;
	}

	public Map<EmailTemplatePart, String> getTemplateParts() {
		return templateParts;
	}

	public EmailTemplate setTemplateParts(Map<EmailTemplatePart, String> templateParts) {
		this.templateParts = templateParts;
		return this;
	}

	public EmailTemplate setTemplatePart(EmailTemplatePart key, String value) {
		templateParts.put(key, value);
		return this;
	}

	public String getCallToAction1URL() {
		return callToAction1URL;
	}

	public EmailTemplate setCallToAction1URL(String url) {
		callToAction1URL = url;
		return this;
	}

	public String getCallToAction2URL() {
		return callToAction2URL;
	}

	public EmailTemplate setCallToAction2URL(String url) {
		callToAction2URL = url;
		return this;
	}

}