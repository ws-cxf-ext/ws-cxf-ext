package org.ws.cxf.ext.interceptor;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ws.cxf.ext.utils.CXFMessageUtils.addHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseOutbound;
import static org.ws.cxf.ext.utils.HTTPUtils.httpBuildQuery;
import static org.ws.cxf.ext.utils.SecurityUtils.generateAuthParameters;
import static org.ws.cxf.ext.utils.Utils.generateSignature;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Authentication interceptor. Client side.
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

		if (isNotBlank(appid)) {
			generateAuthAuthorization(message, uri);
		}

		logAdditionnalTraces(message, LOGGER);
	}

	/**
	 * Generate authorization tokens.
	 * 
	 * @param message
	 * @param uri
	 */
	private void generateAuthAuthorization(Message message, String uri) {
		addHeaderParam(message, "Authorization", generateSignature(appid, env, uri));
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

	/**
	 * @return the appid
	 */
	public String getAppid() {
		return appid;
	}

	/**
	 * @param appid
	 *            the appid to set
	 */
	public void setAppid(String appid) {
		this.appid = appid;
	}

	/**
	 * @return the env
	 */
	public String getEnv() {
		return env;
	}

	/**
	 * @param env
	 *            the env to set
	 */
	public void setEnv(String env) {
		this.env = env;
	}
}
