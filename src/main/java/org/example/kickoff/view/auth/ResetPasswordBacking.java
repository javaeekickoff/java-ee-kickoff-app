package org.example.kickoff.view.auth;

import static org.example.kickoff.model.LoginToken.TokenType.RESET_PASSWORD;
import static org.omnifaces.util.Faces.getRemoteAddr;
import static org.omnifaces.util.Faces.getRequestURL;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Messages.addFlashGlobalInfo;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;
import static org.omnifaces.util.Messages.addGlobalInfo;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.example.kickoff.business.service.PersonService;
import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class ResetPasswordBacking extends AuthBacking {

	@Inject @Param
	private String token;

	@Inject
	private PersonService personService;

	@Inject
	private Logger logger;

	@Override
	@PostConstruct
	public void init() {
		super.init();

		if (token != null && !personService.findByLoginToken(token, RESET_PASSWORD).isPresent()) {
			addFlashGlobalWarn("reset_password.message.warn.invalid_token");
			redirect("reset-password");
		}
	}

	public void requestResetPassword() {
		String email = person.getEmail();
		String ipAddress = getRemoteAddr();

		try {
			personService.requestResetPassword(email, ipAddress, getRequestURL() + "?token=%s");
		}
		catch (Exception e) {
			logger.warning(ipAddress + " made a failed attempt to reset password for email " + email + ": " + e);
		}

		addGlobalInfo("reset_password.message.info.email_sent"); // For security, show success message regardless of outcome.
	}

	public void saveNewPassword() throws IOException {
		personService.updatePassword(token, password);
		addFlashGlobalInfo("reset_password.message.info.password_changed");
		redirect("user/profile");
	}

	public String getToken() {
		return token;
	}

}