package org.example.kickoff.view.user;

import static org.omnifaces.util.Messages.addGlobalInfo;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;
import org.example.kickoff.model.validator.Password;
import org.example.kickoff.view.ActiveUser;

@Named
@RequestScoped
public class ProfileBacking {

	private Person person;
	private @NotNull @Password String currentPassword;
	private @NotNull @Password String newPassword;

	@Inject
	private ActiveUser activeUser;

	@Inject
	private PersonService personService;

	@PostConstruct
	public void init() {
		person = activeUser.get();
	}

	public void save() {
		personService.update(person);
		addGlobalInfo("user_profile.message.info.account_updated");
	}

	public void changePassword() {
		personService.updatePassword(person, newPassword);
		addGlobalInfo("user_profile.message.info.password_changed");
	}

	public Person getPerson() {
		return person;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}