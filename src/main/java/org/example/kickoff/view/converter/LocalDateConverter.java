package org.example.kickoff.view.converter;
import static org.omnifaces.util.Messages.createError;
import static org.omnifaces.util.Utils.coalesce;
import static org.omnifaces.util.Utils.parseLocale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import org.omnifaces.util.FacesLocal;

@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }

        if (modelValue instanceof LocalDate) {
            return getFormatter(context, component).format((LocalDate) modelValue);
        }
        else {
            throw new IllegalArgumentException("This converter can only be used on LocalDate.");
        }
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
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