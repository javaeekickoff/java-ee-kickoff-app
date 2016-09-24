package org.example.kickoff.view.auth;

import static org.omnifaces.util.Faces.redirect;
import static org.omnifaces.util.Messages.addFlashGlobalWarn;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.example.kickoff.model.validator.Email;
import org.example.kickoff.view.ActiveUser;

@Named
@RequestScoped
public class ResetPasswordBacking {

	private @NotNull @Email String email;

	@Inject
	private ActiveUser activeUser;

	@PostConstruct
	public void init() throws IOException {
		if (activeUser.isPresent()) {
			addFlashGlobalWarn("login.message.info.already_logged_in");
			redirect("user/profile");
		}
	}

	public void reset() {
		// TODO
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}