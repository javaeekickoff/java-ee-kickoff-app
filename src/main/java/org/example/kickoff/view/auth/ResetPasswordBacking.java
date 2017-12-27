package org.example.kickoff.view.auth;

import static org.example.kickoff.model.LoginToken.TokenType.RESET_PASSWORD;
import static org.omnifaces.util.Faces.getRemoteAddr;
import static org.omnifaces.util.Faces.getRequestURL;
import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Messages.addFlashGlobalInfo;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;
import static org.omnifaces.util.Messages.addGlobalInfo;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.example.kickoff.business.service.UserService;
import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class ResetPasswordBacking extends AuthBacking {

	@Inject @Param
	private String token;

	@Inject
	private UserService userService;

	@Inject
	private Logger logger;

	@Override
	@PostConstruct
	public void init() {
		super.init();

		if (token != null && !userService.findByLoginToken(token, RESET_PASSWORD).isPresent()) {
			addFlashGlobalWarn("reset_password.message.warn.invalid_token");
			redirect("reset-password");
		}
	}

	public void requestResetPassword() {
		String email = user.getEmail();
		String ipAddress = getRemoteAddr();

		try {
			userService.requestResetPassword(email, ipAddress, getRequestURL() + "?token=%s");
		}
		catch (Exception e) {
			logger.warning(ipAddress + " made a failed attempt to reset password for email " + email + ": " + e);
		}

		addGlobalInfo("reset_password.message.info.email_sent"); // For security, show success message regardless of outcome.
	}

	public void saveNewPassword() {
		userService.updatePassword(token, password);
		addFlashGlobalInfo("reset_password.message.info.password_changed");
		redirect("user/profile");
	}

	public String getToken() {
		return token;
	}

}