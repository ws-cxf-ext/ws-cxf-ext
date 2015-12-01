package org.ws.cxf.ext.interceptor;

import static org.ws.cxf.ext.utils.CXFMessageUtils.addHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getClient;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getCorrelationId;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestMethod;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestReturnCode;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseOutbound;
import static org.ws.cxf.ext.utils.JSONUtils.map2json;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.ws.cxf.ext.correlation.CurrentCorrelationId;

/**
 * Interceptor which aims to log HTTP calls. Client-side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 * 
 */
public class LogClientInterceptor extends CustomAbstractInterceptor {

	private static final Logger LOGGER = LogManager.getLogger(LogClientInterceptor.class);

	private static final String PARAM_CLIENT_NAME = "clientName";
	private static final String PARAM_SERVICE = "service";
	private static final String PARAM_METHOD = "method";
	private static final String PARAM_LOGIN_USER = "loginUser";
	private static final String PARAM_CORRELATION_ID = "correlationId";

	private static final String INFO_CODE = "code";
	private static final String INFO_CLIENT_NAME = "client-name";
	private static final String INFO_PHASE = "phase";
	private static final String INFO_CORRELATION_ID = "correlationId";

	private static String ERR_NO_DEF_SECURITY_CONTEXT = "Pas de context Spring security pour le client... : %s";

	private CurrentCorrelationId currentCorrelationId;

	/**
	 * Constructor.
	 */
	public LogClientInterceptor(String phase) {
		super(null == phase ? Phase.SEND : phase);
	}

	/**
	 * Default constructor.
	 */
	public LogClientInterceptor() {
		this(null);
	}

	/**
	 * Getting the login of user from Spring Security context.
	 * 
	 * @return String
	 */
	private String getLoginUser() {
		SecurityContext context = getSecurityContext();
		if (context == null || context.getAuthentication() == null || context.getAuthentication().getPrincipal() == null || context.getAuthentication().getPrincipal() instanceof String) {
			return null;
		}
		UserDetails user = (UserDetails) context.getAuthentication().getPrincipal();
		return StringUtils.isNotBlank(user.getUsername()) ? user.getUsername() : null;
	}

	/**
	 * Logging out message.
	 * 
	 * @param message
	 */
	public void handleMessageOut(Message message) {
		String correlationId = (null == currentCorrelationId) ? null : getCorrelationId(message, currentCorrelationId.getCurrentCorrelationId());
		String clientName = (null == currentCorrelationId) ? null : getClient(message, currentCorrelationId.getCurrentClient());

		Map<String, String> infos = new HashMap<String, String>();

		infos.put(INFO_CORRELATION_ID, correlationId);
		infos.put(INFO_PHASE, getPhase());
		infos.put(PARAM_METHOD, getRequestMethod(message));

		String uri = getRequestURI(message, false);

		infos.put(PARAM_SERVICE, uri);
		infos.put(INFO_CLIENT_NAME, clientName);

		addHeaderParam(message, PARAM_CORRELATION_ID, correlationId);
		addHeaderParam(message, PARAM_CLIENT_NAME, clientName);
		addHeaderParam(message, PARAM_LOGIN_USER, getLoginUser());

		String jsonLog = map2json(infos);

		LOGGER.info(jsonLog);
		logAdditionnalTraces(message);
	}

	/**
	 * Logging in message.
	 * 
	 * @param message
	 */
	public void handleMessageIn(Message message) {
		String correlationId = null == currentCorrelationId ? null : getCorrelationId(message, currentCorrelationId.getCurrentCorrelationId());
		String clientName = null == currentCorrelationId ? null : getClient(message, currentCorrelationId.getCurrentClient());

		Map<String, String> infos = new HashMap<String, String>();
		infos.put(INFO_CORRELATION_ID, correlationId);
		infos.put(INFO_PHASE, getPhase());
		infos.put(INFO_CODE, String.valueOf(getRequestReturnCode(message)));
		infos.put(INFO_CLIENT_NAME, clientName);

		String jsonLog = map2json(infos);

		LOGGER.info(jsonLog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(Message message) {
		if (isPhaseOutbound(getPhase())) {
			handleMessageOut(message);
		} else {
			handleMessageIn(message);
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

	/**
	 * @return the securityContext
	 */
	public SecurityContext getSecurityContext() {
		try {
			SecurityContext ctx = SecurityContextHolder.getContext();
			if (null == ctx) {
				ctx = new CustomSecurityContextStub();
			}

			return ctx;
		} catch (Exception e) {
			LOGGER.debug(String.format(ERR_NO_DEF_SECURITY_CONTEXT, e.getMessage()), e);
			return new CustomSecurityContextStub();
		}
	}

}
