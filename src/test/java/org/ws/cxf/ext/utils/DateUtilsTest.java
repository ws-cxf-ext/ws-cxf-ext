package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertEquals;
import static org.ws.cxf.ext.utils.DateUtils.calendarToString;
import static org.ws.cxf.ext.utils.DateUtils.getCurrentTime;
import static org.ws.cxf.ext.utils.DateUtils.stringToCalendar;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.ws.cxf.ext.AbstractTest;

/**
 * DateUtils class tests.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class DateUtilsTest extends AbstractTest {

	/**
	 * Test getting current date.
	 */
	@Test
	public final void testGetCurrentTime() {
		assertEquals(calendarToString(Calendar.getInstance()), calendarToString(getCurrentTime()));
	}

	/**
	 * Test converting calendar to String.
	 */
	@Test
	public final void testCalendarToString() {
		Calendar d = new GregorianCalendar();
		d.set(Calendar.YEAR, 2014);
		d.set(Calendar.MONTH, Calendar.SEPTEMBER);
		d.set(Calendar.DAY_OF_MONTH, 5);

		assertEquals("05/09/2014", calendarToString(d));
	}

	/**
	 * Test converting String to calendar.
	 */
	@Test
	public final void testStringToCalendar() {
		try {
			Calendar d = stringToCalendar("05/09/2014");
			assertEquals(2014, d.get(Calendar.YEAR));
			assertEquals(Calendar.SEPTEMBER, d.get(Calendar.MONTH));
			assertEquals(5, Calendar.DAY_OF_MONTH);
		} catch (ParseException e) {
			failWithException(e);
		}
	}

	/**
	 * Test StringToCalendar.
	 */
	@Test
	public final void testStringToCalendarWithHourMinutes() {
		try {

			Calendar d1 = stringToCalendar("05/09/2014", "dd/MM/yyyy");
			assertEquals(5, d1.get(Calendar.DAY_OF_MONTH));
			assertEquals(8, d1.get(Calendar.MONTH));
			assertEquals(2014, d1.get(Calendar.YEAR));
			assertEquals(0, d1.get(Calendar.HOUR));
			assertEquals(0, d1.get(Calendar.MINUTE));
			assertEquals(0, d1.get(Calendar.SECOND));
		} catch (ParseException e) {
			failWithException(e);
		}
	}

}
