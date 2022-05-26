package org.ws.cxf.ext.interceptor;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseInbound;
import static org.ws.cxf.ext.utils.Utils.checkSignature;
import static org.ws.cxf.ext.utils.Utils.getBasicAuth;
import static org.ws.cxf.ext.utils.Utils.getServiceException;
import static org.ws.cxf.ext.utils.Utils.populateHashByAppid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAuthorizedException;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.ws.cxf.ext.appid.ICurrentAppId;
import org.ws.cxf.ext.auth.CustomBasicAuth;
import org.ws.cxf.ext.auth.ExceptionAuth;
import org.ws.cxf.ext.utils.CheckStatus;

/**
 * Authentication interceptor. Server side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 * 
 */
public class AuthServerInterceptor extends CustomAbstractInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerInterceptor.class);

	/**
	 * Disable auth flag.
	 */
	@Value("${ws.disable.auth:false}")
	private boolean disableAuthParam;

	/**
	 * Application environment.
	 */
	@Value("${ws.env.auth:dev}")
	private String env;

	@Value("${ws.subpath.to.substract:#{''}}")
	private String subpathToSubstract;

	@Autowired
	private ICurrentAppId currentAppId;

	/**
	 * Autorizations
	 */
	private CustomBasicAuth getAuth;
	private CustomBasicAuth postAuth;
	private CustomBasicAuth putAuth;
	private CustomBasicAuth deleteAuth;

	private Map<String, String> hashByAppid;

	/**
	 * Default constructor.
	 */
	public AuthServerInterceptor() {
		this(null);
	}

	/**
	 * Constuctor.
	 * 
	 * @param phase
	 */
	public AuthServerInterceptor(String phase) {
		super((null == phase) ? Phase.RECEIVE : phase);
	}

	/**
	 * Initialization of bean after IoC.
	 */
	@PostConstruct
	public void init() {
		hashByAppid = new HashMap<>();
		populateHashByAppid(getAuth, env, hashByAppid);
		populateHashByAppid(postAuth, env, hashByAppid);
		populateHashByAppid(putAuth, env, hashByAppid);
		populateHashByAppid(deleteAuth, env, hashByAppid);
	}

	/**
	 * Handle in calling.
	 * 
	 * @param message
	 */
	public void handleMessageIn(Message message) {
		checkAuth(message);
	}

	/**
	 * Handle out calling.
	 */
	public void handleMessageOut(Message message) {

	}

	/**
	 * Checking authorization.
	 * 
	 * @param message
	 */
	private void checkAuth(Message message) {
		String authorization = getHeaderParam(message, "Authorization");
		String service = getRequestURI(message, subpathToSubstract);
		Optional<CustomBasicAuth> auth = getBasicAuth(message, getAuth, postAuth, putAuth, deleteAuth);
		Optional<ExceptionAuth> exp = getServiceException(auth, service);
		CheckStatus status = checkSignature(disableAuthParam, env, authorization, service, auth, exp, hashByAppid, currentAppId);

		// We retry without removing the subpath
		if(!status.isOk()) {
			service = getRequestURI(message, EMPTY);
			exp = getServiceException(auth, service);
			status = checkSignature(disableAuthParam, env, authorization, service, auth, exp, hashByAppid, currentAppId);
		}

		if(!status.isOk()) {
			LOGGER.warn(status.getMessage());
			throw new NotAuthorizedException(status.getMessage());
		}
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
	 * @return the getAuth
	 */
	public CustomBasicAuth getGetAuth() {
		return getAuth;
	}

	/**
	 * @param getAuth
	 *            the getAuth to set
	 */
	public void setGetAuth(CustomBasicAuth getAuth) {
		this.getAuth = getAuth;
	}

	/**
	 * @return the postAuth
	 */
	public CustomBasicAuth getPostAuth() {
		return postAuth;
	}

	/**
	 * @param postAuth
	 *            the postAuth to set
	 */
	public void setPostAuth(CustomBasicAuth postAuth) {
		this.postAuth = postAuth;
	}

	/**
	 * @return the putAuth
	 */
	public CustomBasicAuth getPutAuth() {
		return putAuth;
	}

	/**
	 * @param putAuth
	 *            the putAuth to set
	 */
	public void setPutAuth(CustomBasicAuth putAuth) {
		this.putAuth = putAuth;
	}

	/**
	 * @return the deleteAuth
	 */
	public CustomBasicAuth getDeleteAuth() {
		return deleteAuth;
	}

	/**
	 * @param deleteAuth
	 *            the deleteAuth to set
	 */
	public void setDeleteAuth(CustomBasicAuth deleteAuth) {
		this.deleteAuth = deleteAuth;
	}

	/**
	 * @return the disableAuthParam
	 */
	public boolean isDisableAuthParam() {
		return disableAuthParam;
	}

	/**
	 * @param disableAuthParam
	 *            the disableAuthParam to set
	 */
	public void setDisableAuthParam(boolean disableAuthParam) {
		this.disableAuthParam = disableAuthParam;
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
