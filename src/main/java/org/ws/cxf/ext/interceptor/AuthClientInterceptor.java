package org.ws.cxf.ext.interceptor;

import static org.ws.cxf.ext.utils.CXFMessageUtils.addHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseOutbound;
import static org.ws.cxf.ext.utils.HTTPUtils.httpBuildQuery;
import static org.ws.cxf.ext.utils.SecurityUtils.generateAuthParameters;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Authentification interceptor. Client side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class AuthClientInterceptor extends CustomAbstractInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthClientInterceptor.class);

	/**
	 * Application secret.
	 */
	@Value("${ws.appid.auth}")
	private String appid;

	/**
	 * Application environment.
	 */
	@Value("${ws.env.auth:dev}")
	private String env;

	/**
	 * Constructor.
	 * 
	 * @param phase
	 */
	public AuthClientInterceptor(String phase) {
		super((null == phase) ? Phase.SEND : phase);
	}

	/**
	 * Default constructor.
	 */
	public AuthClientInterceptor() {
		this(null);
	}

	/**
	 * Handle out calling.
	 * 
	 * @param message
	 */
	public void handleMessageOut(Message message) {

		String uri = getRequestURI(message, false);

		if (null != appid) {
			generateOAuthAuthorization(message, uri);
		}

		logAdditionnalTraces(message, LOGGER);
	}

	/**
	 * Generate authorization tokens.
	 * 
	 * @param message
	 * @param uri
	 */
	private void generateOAuthAuthorization(Message message, String uri) {
		addHeaderParam(message, "Authorization", "Auth " + httpBuildQuery(generateAuthParameters(appid, env, uri)));
	}

	/**
	 * Handle in calling.
	 * 
	 * @param message
	 */
	public void handleMessageIn(Message message) {

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

}
