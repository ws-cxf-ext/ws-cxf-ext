package org.ws.cxf.ext.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CXF technical exception.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class CxfExtraTechnicalException extends RuntimeException {

	private static final Logger LOGGER = LogManager.getLogger(CxfExtraTechnicalException.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Logging constructor.
	 * 
	 * @param e
	 */
	public CxfExtraTechnicalException(Exception e) {
		LOGGER.error(e);
	}

}
