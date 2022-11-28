package org.example.kickoff.view.converter;

import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;

import org.omnifaces.converter.SelectItemsConverter;
import org.omnifaces.persistence.model.BaseEntity;

@FacesConverter("baseEntitySelectItemsConverter")
public class BaseEntitySelectItemsConverter extends SelectItemsConverter {

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof BaseEntity) {
			Object id = ((BaseEntity<?>) value).getId();
			return Objects.toString(id, "");
		}
		else {
			return super.getAsString(context, component, value);
		}
	}

}