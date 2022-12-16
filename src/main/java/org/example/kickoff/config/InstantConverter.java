package org.example.kickoff.config;
 import java.time.Instant;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

 /**
  * Converts the JDK 8 / JSR 310 <code>Instant</code> type to <code>Date</code> for
  * usage with JPA.
  *
  * <p>
  * This converter is still necessary for Jakarta EE 10 / Jakarta Persistence 3.1, since Instant remains to be one of the
  * "forgotten" types.
  *
  * See https://github.com/jakartaee/persistence/issues/163
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