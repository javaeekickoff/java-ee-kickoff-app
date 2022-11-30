package org.example.kickoff.view.admin;

import static org.omnifaces.util.Messages.addGlobalWarn;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.optimusfaces.model.PagedDataModel;

@Named
@ViewScoped
public class UsersBacking implements Serializable {

	private static final long serialVersionUID = 1L;

	private PagedDataModel<Person> model;

	@Inject
	private PersonService personService;

	@PostConstruct
	public void init() {
		model = PagedDataModel.lazy(personService).build();
	}

	public void delete(Person person) {
		// personService.delete(person);
		addGlobalWarn("This is just a demo, we won't actually delete users for now.");
	}

	public PagedDataModel<Person> getModel() {
		return model;
	}

}