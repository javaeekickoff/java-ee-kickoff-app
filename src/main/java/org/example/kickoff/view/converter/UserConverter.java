package org.example.kickoff.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.example.kickoff.business.service.UserService;
import org.example.kickoff.model.User;

@FacesConverter(forClass=User.class, managed=true)
public class UserConverter implements Converter<User> {

	@Inject
	private UserService userService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, User user) {
		if (user == null) {
			return "";
		}

		return user.getId().toString();
	}

	@Override
	public User getAsObject(FacesContext context, UIComponent component, String id) {
		if (id == null) {
			return null;
		}

		try {
			return userService.getById(Long.valueOf(id));
		}
		catch (NumberFormatException e) {
			throw new ConverterException(id + " is not a valid User ID", e);
		}
	}

}