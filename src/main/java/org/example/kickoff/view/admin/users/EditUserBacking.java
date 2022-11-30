package org.example.kickoff.view.admin.users;

import static org.omnifaces.util.Faces.redirect;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;
import org.omnifaces.cdi.Param;

@Named
@RequestScoped
public class EditUserBacking {

	@Inject @Param(name="id")
	private Person person;

	@Inject
	private PersonService personService;

	public void save() throws IOException {
		personService.update(person);
		redirect("admin/users");
	}

	public Person getPerson() {
		return person;
	}

}