package org.ws.cxf.ext.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CXF technical exception.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class CxfExtraTechnicalException extends RuntimeException {
	private static final Logger LOGGER = LoggerFactory.getLogger(CxfExtraTechnicalException.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Logging constructor.
	 * 
	 * @param e
	 */
	public CxfExtraTechnicalException(Exception e) {
		LOGGER.error("Technical error", e);
	}

}
