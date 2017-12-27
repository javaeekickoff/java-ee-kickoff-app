package org.example.kickoff.view.converter;

import static org.omnifaces.util.Messages.createError;
import static org.omnifaces.util.Utils.coalesce;
import static org.omnifaces.util.Utils.parseLocale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.omnifaces.util.FacesLocal;

@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter<LocalDate> {

	@Override
	public String getAsString(FacesContext context, UIComponent component, LocalDate modelValue) {
		if (modelValue == null) {
			return "";
		}

		return getFormatter(context, component).format(modelValue);
	}

	@Override
	public LocalDate getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null || submittedValue.isEmpty()) {
			return null;
		}

		try {
			return LocalDate.parse(submittedValue, getFormatter(context, component));
		}
		catch (DateTimeParseException e) {
			throw new ConverterException(createError("localDateConverter", submittedValue), e);
		}
	}

	private DateTimeFormatter getFormatter(FacesContext context, UIComponent component) {
		return DateTimeFormatter.ofPattern(getPattern(component), getLocale(context, component));
	}

	private String getPattern(UIComponent component) {
		String pattern = (String) component.getAttributes().get("pattern");

		if (pattern == null) {
			throw new IllegalArgumentException("The 'pattern' attribute is required");
		}

		return pattern;
	}

	private Locale getLocale(FacesContext context, UIComponent component) {
		return coalesce(parseLocale(component.getAttributes().get("locale")), FacesLocal.getLocale(context));
	}

}