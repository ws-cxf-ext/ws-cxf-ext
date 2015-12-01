package org.ws.cxf.ext.interceptor;

import static org.ws.cxf.ext.utils.CXFMessageUtils.getClient;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getCorrelationId;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRemoteAddrQuietly;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestMethod;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestReturnCode;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseInbound;
import static org.ws.cxf.ext.utils.JSONUtils.map2json;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ws.cxf.ext.correlation.CurrentCorrelationId;

/**
 * Interceptor which aims to log HTTP calls. Server-side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 * 
 */
public class LogServerInterceptor extends CustomAbstractInterceptor {

	private static final String PARAM_LOGIN_USER = "loginUser";

	private static final String PARAM_CLIENT_NAME = "clientName";

	private static final String PARAM_CORRELATION_ID = "correlationId";

	private static final Logger LOGGER = LogManager.getLogger(LogServerInterceptor.class);

	private CurrentCorrelationId currentCorrelationId;

	/**
	 * Constructor.
	 */
	public LogServerInterceptor(String phase) {
		super((null == phase) ? Phase.RECEIVE : phase);
	}

	/**
	 * Default constructor.
	 */
	public LogServerInterceptor() {
		this(null);
	}

	/**
	 * Inititializing after IoC.
	 */
	@PostConstruct
	public void init() {

	}

	/**
	 * Logging in message.
	 * 
	 * @param message
	 */
	public void handleMessageIn(Message message) {
		String correlationId = getCorrelationId(message, getHeaderParam(message, PARAM_CORRELATION_ID));
		String clientName = getClient(message, getHeaderParam(message, PARAM_CLIENT_NAME));
		String loginUser = getHeaderParam(message, PARAM_LOGIN_USER);

		if (null != currentCorrelationId) {
			currentCorrelationId.setCurrentCorrelationId(correlationId);
		}

		if (null != clientName) {
			currentCorrelationId.setCurrentClient(clientName);
		}

		if (null != loginUser) {
			currentCorrelationId.setCurrentUser(loginUser);
		}

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(PARAM_CORRELATION_ID, correlationId);
		infos.put("phase", getPhase());
		infos.put("method", getRequestMethod(message));
		infos.put("service", getRequestURI(message, true));
		infos.put("client-host", getRemoteAddrQuietly(message));
		infos.put("client-name", clientName);

		String jsonLog = map2json(infos);

		LOGGER.info(jsonLog);
		logAdditionnalTraces(message);
	}

	/**
	 * Logging out message.
	 * 
	 * @param message
	 */
	public void handleMessageOut(Message message) {
		String correlationId = getCorrelationId(message, null);
		String clientName = getClient(message, getHeaderParam(message, PARAM_CLIENT_NAME));

		if (null != currentCorrelationId) {
			currentCorrelationId.setCurrentCorrelationId(correlationId);
		}

		if (null != clientName) {
			currentCorrelationId.setCurrentClient(clientName);
		}

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(PARAM_CORRELATION_ID, getCorrelationId(message, correlationId));
		infos.put("phase", getPhase());
		infos.put("code", String.valueOf(getRequestReturnCode(message)));
		infos.put("client-name", clientName);

		String jsonLog = map2json(infos);

		LOGGER.info(jsonLog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(Message message) {
		if (isPhaseInbound(this.getPhase())) {
			handleMessageIn(message);
		} else {
			handleMessageOut(message);
		}
	}

	/**
	 * @return the currentCorrelationId
	 */
	public CurrentCorrelationId getCurrentCorrelationId() {
		return currentCorrelationId;
	}

	/**
	 * @param currentCorrelationId
	 *            the currentCorrelationId to set
	 */
	public void setCurrentCorrelationId(CurrentCorrelationId currentCorrelationId) {
		this.currentCorrelationId = currentCorrelationId;
	}

}
