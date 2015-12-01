package org.ws.cxf.ext.utils.others;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ws.cxf.ext.Constants;

/**
 * Marshalling/unmarshalling of calendars.
 * 
 * @author Christian Cougourdan
 */
public class CalendarProvider implements ParamConverterProvider {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (Calendar.class.isAssignableFrom(rawType)) {
			return new ParamConverter<T>() {

				private final DateTimeFormatter format = DateTimeFormat.forPattern(Constants.DateTime.FORMAT_ISO_DATETIME);

				/**
				 * {@inheritDoc}
				 */
				@Override
				public T fromString(String value) {
					return rawType.cast(format.parseDateTime(value).toGregorianCalendar());
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public String toString(T value) {
					return new DateTime((Calendar) value).toString(format);
				}

			};
		} else {
			return null;
		}
	}

}
