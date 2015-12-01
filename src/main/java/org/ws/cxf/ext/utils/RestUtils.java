package org.ws.cxf.ext.utils;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils class for RESTful errors or checks.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public final class RestUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class);

	/**
	 * Static class : private constructor.
	 */
	private RestUtils() {

	}

	/**
	 * Checking not empty param, throwing BadRequest (400) otherwise.
	 * 
	 * @param value
	 * @param msg
	 */
	public static void assertParamNotEmpty(String value, String msg) {
		if (StringUtils.isEmpty(value)) {
			LOGGER.debug("[assertParamNotEmpty] message = " + msg);
			throw new BadRequestException(msg);
		}
	}

	/**
	 * Checking empty param, throwing BadRequest (400) otherwise.
	 * 
	 * @param value
	 * @param msg
	 */
	public static void assertParamEmpty(String value, String msg) {
		if (StringUtils.isNotEmpty(value)) {
			LOGGER.debug("[assertParamEmpty] message = " + msg);
			throw new BadRequestException(msg);
		}
	}

	/**
	 * Checking not empty param, throwing BadRequest (400) otherwise.
	 * 
	 * @param value
	 * @param msg
	 */
	public static void assertParamNotEmpty(Object value, String msg) {
		if (null == value) {
			LOGGER.debug("[assertParamNotEmpty] message = " + msg);
			throw new BadRequestException(msg);
		}
	}

	/**
	 * Checking empty param, throwing BadRequest (400) otherwise.
	 * 
	 * @param value
	 * @param msg
	 */
	public static void assertParamEmpty(Object value, String msg) {
		if (null != value) {
			LOGGER.debug("[assertParamEmpty] message = " + msg);
			throw new BadRequestException(msg);
		}
	}

}
