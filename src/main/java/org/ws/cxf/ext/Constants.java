package org.ws.cxf.ext;

/**
 * Constants interface.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public interface Constants {
	/** Technical user */
	String TECH_USER = "system";

	/**
	 * Others
	 */
	String EMPTY_STRING = "";
	String EMPTY_JSON = "[]";

	/**
	 * HTTP methods
	 */
	String HTTP_GET = "GET";
	String HTTP_POST = "POST";
	String HTTP_PUT = "PUT";
	String HTTP_DELETE = "DELETE";

	public static final class DateTime {

		public static final String FORMAT_DATE_HOUR = "dd/MM/yyyy HH:mm:ss";

		public static final String FORMAT_DATE_HOUR_MINUTE = "dd/MM/yyyy HH:mm";

		public static final String FORMAT_ISO_DATETIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";

		private DateTime() {
		}

	}
}
