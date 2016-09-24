package org.example.kickoff.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@FacesConverter(forClass=User.class)
public class UserConverter implements Converter {

	@Inject
	private UserService userService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		if (modelValue == null) {
			return "";
		}

		if (modelValue instanceof User) {
			return ((User) modelValue).getId().toString();
		}
		else {
			throw new ConverterException(modelValue + " is not an User");
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null) {
			return null;
		}

		try {
			return userService.getById(Long.valueOf(submittedValue));
		}
		catch (NumberFormatException e) {
			throw new ConverterException(submittedValue + " is not a valid User ID", e);
		}
	}

}