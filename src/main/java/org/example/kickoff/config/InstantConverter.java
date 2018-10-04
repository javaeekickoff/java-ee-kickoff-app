package org.example.kickoff.config;
import java.time.Instant;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts the JDK 8 / JSR 310 <code>Instant</code> type to <code>Date</code> for
 * usage with JPA.
 * 
 * <p>
 * This converter is still necessary for Java EE 8 / JPA 2.2, since Instant is one of the
 * "forgotten" types.
 * 
 * See https://github.com/eclipse-ee4j/jpa-api/issues/163
 * 
 * @author Arjan Tijms
 */
@Converter(autoApply = true)
public class InstantConverter implements AttributeConverter<Instant, Date> {

	@Override
	public Date convertToDatabaseColumn(Instant instant) {
		if (instant == null) {
			return null;
		}
		
		return Date.from(instant);
	}

	@Override
	public Instant convertToEntityAttribute(Date date) {
		if (date == null) {
			return null;
		}
		
		return date.toInstant();
	}
}