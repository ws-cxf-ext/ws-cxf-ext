package org.ws.cxf.ext.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utils date classes.
 *
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class DateUtils {
	public static final String FORMAT_DATE = "dd/MM/yyyy";

	/**
	 * Static classe : private constructor.
	 */
	private DateUtils() {

	}

	/**
	 * Convert DD/MM/YYYY string to Calendar.
	 * 
	 * @param str
	 * @param lenient
	 * @return Calendar
	 * @throws ParseException
	 */
	public static final Calendar stringToCalendar(final String str, boolean lenient) throws ParseException {
		return stringToCalendar(str, FORMAT_DATE, lenient);
	}

	/**
	 * Convert string to Calendar.
	 * 
	 * @param str
	 * @param format
	 * @param lenient
	 * @return Calendar
	 * @throws ParseException
	 */
	public static final Calendar stringToCalendar(final String str, String format, boolean lenient) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
		sdf.setLenient(lenient);
		Date date = sdf.parse(str);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Convert DD/MM/YYYY string to calendar (lenient is active).
	 * 
	 * @param str
	 * @return Calendar
	 * @throws ParseException
	 */
	public static final Calendar stringToCalendar(final String str) throws ParseException {
		return stringToCalendar(str, true);
	}

	/**
	 * Convert string to calendar (lenient is active).
	 * 
	 * @param str
	 * @param format
	 * @return Calendar
	 * @throws ParseException
	 */
	public static final Calendar stringToCalendar(final String str, String format) throws ParseException {
		return stringToCalendar(str, format, true);
	}

	/**
	 * Convert calendar to string.
	 * 
	 * @param c
	 *            : calendar
	 * @param format
	 * @return String
	 */
	public static final String calendarToString(Calendar c, String format) {
		String rtn = null;

		if (c != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
			rtn = sdf.format(c.getTime());
		}

		return rtn;
	}

	/**
	 * Convert calendar to DD/MM/YYYY string
	 * 
	 * @paramc : calendar
	 * @return String
	 */
	public static final String calendarToString(Calendar c) {
		return calendarToString(c, FORMAT_DATE);
	}

	/**
	 * Getting the current date.
	 * 
	 * @return Calendar
	 */
	public static final Calendar getCurrentTime() {
		return Calendar.getInstance();
	}
}
