package org.example.kickoff.view.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import org.example.kickoff.business.service.PersonService;
import org.example.kickoff.model.Person;

@FacesConverter(forClass=Person.class)
public class PersonConverter implements Converter {

	@Inject
	private PersonService personService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		if (modelValue == null) {
			return "";
		}

		if (modelValue instanceof Person) {
			return ((Person) modelValue).getId().toString();
		}
		else {
			throw new ConverterException(modelValue + " is not a Person");
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null) {
			return null;
		}

		try {
			return personService.getById(Long.valueOf(submittedValue));
		}
		catch (NumberFormatException e) {
			throw new ConverterException(submittedValue + " is not a valid Person ID", e);
		}
	}

}