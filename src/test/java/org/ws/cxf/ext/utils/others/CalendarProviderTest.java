package org.ws.cxf.ext.utils.others;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ws.rs.ext.ParamConverter;

import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * CalendarProvider's tests.
 * 
 * @author Christian Cougourdan
 */
public class CalendarProviderTest {

	/**
	 * testing converter factory.
	 */
	@Test
	public void testConverterFactory() {
		CalendarProvider provider = new CalendarProvider();

		ServerProviderFactory factory = ServerProviderFactory.getInstance();
		factory.clearProviders();

		assertNull(factory.createParameterHandler(Calendar.class, null, null));

		factory.registerUserProvider(provider);

		assertNotNull(factory.createParameterHandler(Calendar.class, null, null));
		assertNotNull(factory.createParameterHandler(GregorianCalendar.class, null, null));

		assertNull(factory.createParameterHandler(String.class, null, null));
	}

	/**
	 * testing calendar conversion.
	 */
	@Test
	public void testCalendarConversion() {
		CalendarProvider provider = new CalendarProvider();

		ParamConverter<Calendar> converter = provider.getConverter(Calendar.class, null, null);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);

		assertNotNull(converter);
		assertEquals("Conversion to string", new DateTime(calendar).toString("yyyy-MM-dd'T'HH:mm:ss'Z'"), converter.toString(calendar));
		assertEquals("Parsing error", calendar.getTimeInMillis(), converter.fromString(new DateTime(calendar).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")).getTimeInMillis());

		calendar.set(Calendar.MILLISECOND, 1);
		assertEquals("Millisecond not signifiant", calendar.getTimeInMillis() - 1L, converter.fromString(new DateTime(calendar).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")).getTimeInMillis());
	}

}
