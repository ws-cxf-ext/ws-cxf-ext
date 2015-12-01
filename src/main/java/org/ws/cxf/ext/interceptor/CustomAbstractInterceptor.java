package org.ws.cxf.ext.interceptor;

import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getQueryHeader;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getQueryStringQuietly;
import static org.ws.cxf.ext.utils.JSONUtils.map2json;

import java.util.List;
import java.util.Map;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract interceptor.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public abstract class CustomAbstractInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger LOGGER = LogManager.getLogger(CustomAbstractInterceptor.class);

	/**
	 * Constructor.
	 * 
	 * @param phase
	 */
	public CustomAbstractInterceptor(String phase) {
		super(phase);
	}

	/**
	 * Logging headers.
	 * 
	 * @param message
	 * @param logger
	 */
	public void logHeaders(Message message, Logger logger) {
		Map<String, List<String>> headers = getQueryHeader(message);
		if (isNotEmpty(headers)) {
			logger.debug("HEADER params : " + map2json(headers));
		}
	}

	/**
	 * Logging additional traces.
	 * 
	 * @param message
	 * @param logger
	 */
	public void logAdditionnalTraces(Message message, Logger logger) {
		logger.debug("GET params : " + getQueryStringQuietly(message));
		logHeaders(message, logger);
	}

	/**
	 * Logging additional traces.
	 * 
	 * @param message
	 */
	public void logAdditionnalTraces(Message message) {
		logAdditionnalTraces(message, LOGGER);
	}

}
