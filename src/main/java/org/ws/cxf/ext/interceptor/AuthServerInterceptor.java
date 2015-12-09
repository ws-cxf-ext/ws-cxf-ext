package org.ws.cxf.ext.interceptor;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getHeaderParam;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestMethod;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestURI;
import static org.ws.cxf.ext.utils.CXFMessageUtils.isPhaseInbound;
import static org.ws.cxf.ext.utils.HTTPUtils.getQueryMap;
import static org.ws.cxf.ext.utils.SecurityUtils.getSHA1Hmac;
import static org.ws.cxf.ext.utils.SecurityUtils.isEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAuthorizedException;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ws.cxf.ext.Constants;
import org.ws.cxf.ext.auth.CustomBasicAuth;

/**
 * Authentification interceptor. Server side.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 * 
 */
public class AuthServerInterceptor extends CustomAbstractInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerInterceptor.class);

	private static final String PARAM_CHARSET = "UTF-8";

	/**
	 * Disable auth flag.
	 */
	private boolean disableAuthParam;

	/**
	 * Application environment.
	 */
	private String env;

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
	 * Populate map from appids.
	 * 
	 * @param auth
	 */
	private void populateHashByAppid(CustomBasicAuth auth) {
		if (isEmpty(auth.getAppids())) {
			return;
		}

		for (String appid : auth.getAppids()) {
			if (hashByAppid.containsKey(appid)) {
				continue;
			}

			hashByAppid.put(appid, getHashFromAppid(appid));
		}
	}

	/**
	 * Initialization of bean after IoC.
	 */
	@PostConstruct
	public void init() {
		hashByAppid = new HashMap<String, String>();
		populateHashByAppid(getAuth);
		populateHashByAppid(postAuth);
		populateHashByAppid(putAuth);
		populateHashByAppid(deleteAuth);
	}

	/**
	 * Getting hash from cache or regenerate.
	 * 
	 * @param appid
	 * @return String
	 */
	private String getHashFromAppid(String appid) {
		if (null == hashByAppid) {
			hashByAppid = new HashMap<String, String>();
		}

		if (!hashByAppid.containsKey(appid)) {
			hashByAppid.put(appid, getSHA1Hmac(appid, env));
		}

		return hashByAppid.get(appid);
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
	 * Return true if the service isn't authentified, otherwise false.
	 * 
	 * @param auth
	 * @param service
	 * @return boolean
	 */
	private boolean isServiceNotAuthentified(CustomBasicAuth auth, String service) {
		if (isEmpty(service) || isEmpty(auth.getExceptions())) {
			return false;
		}

		for (String e : auth.getExceptions()) {
			if (service.toLowerCase().contains(e.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checking authorization.
	 * 
	 * @param message
	 */
	private void checkAuth(Message message) {
		String authorization = getHeaderParam(message, "Authorization");

		String service = getRequestURI(message, true);
		CustomBasicAuth auth = getBasicAuth(message);
		if (disableAuthParam || isServiceNotAuthentified(auth, service)) {
			LOGGER.warn("Sécurité forcée pour le service : {}", service);
			return;
		}

		if (isEmpty(authorization)) {
			LOGGER.warn("Pas d'informations d'authentification pour le service : {}", service);
			throw new NotAuthorizedException("Authentification error");
		}

		Map<String, String> authParams = getQueryMap(authorization);
		String hashConsumerKey = null;
		String hashSignature = null;
		String tokenDecode = null;

		// Parameter checking
		String consumerKey = authParams.get("auth_consumer_key");
		String signature = authParams.get("auth_signature");
		String token = authParams.get("auth_token");

		checkAuthParam("auth_consumer_key", consumerKey);
		checkAuthParam("auth_token", token);
		checkAuthParam("auth_callback", authParams.get("auth_callback"));
		checkAuthParam("auth_signature", signature);
		checkAuthParam("auth_nonce", authParams.get("auth_nonce"));
		checkAuthParam("auth_timestamp", authParams.get("auth_timestamp"));
		checkAuthParam("auth_signature_method", authParams.get("auth_signature_method"), "HMAC-SHA1");

		try {
			hashConsumerKey = URLDecoder.decode(consumerKey, PARAM_CHARSET);
			hashSignature = URLDecoder.decode(signature, PARAM_CHARSET);
			tokenDecode = URLDecoder.decode(token, PARAM_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new NotAuthorizedException("Authentification error : UnsupportedEncodingException " + e.getMessage(), e);
		}

		if (null != auth) {
			boolean needErr401 = true;

			if (null != hashConsumerKey && isNotEmpty(auth.getAppids())) {
				needErr401 = !auth.getAppids().stream().anyMatch(isExpectedAuth(service + tokenDecode, hashConsumerKey, hashSignature));
			}

			if (needErr401) {
				LOGGER.warn("Unknown consumer : " + hashConsumerKey);
				throw new NotAuthorizedException("Authentification error");
			}
		}
	}

	/**
	 * Predicate : is expected auth ?
	 * 
	 * @param service
	 * @param hashConsumerKey
	 * @param hashSignature
	 * @return Predicate<String>
	 */
	private Predicate<String> isExpectedAuth(String service, String hashConsumerKey, String hashSignature) {
		return appid -> {
			String hashConsumerKeyExpected = getHashFromAppid(appid);
			String hashSignatureExpected = getSHA1Hmac(appid, service);
			return isEquals(hashConsumerKey, hashConsumerKeyExpected) && isEquals(hashSignature, hashSignatureExpected);
		};
	}

	/**
	 * Check param.
	 * 
	 * @param paramName
	 * @param paramValue
	 * @throws NotAuthorizedException
	 */
	private void checkAuthParam(String paramName, String paramValue) {
		checkAuthParam(paramName, paramValue, null);
	}

	/**
	 * Checking single parameter.
	 * 
	 * @param paramName
	 * @param paramValue
	 * @param expectedValue
	 * @throws NotAuthorizedException
	 */
	private void checkAuthParam(String paramName, String paramValue, String expectedValue) {
		if (null == paramValue || (null != expectedValue && !expectedValue.equalsIgnoreCase(paramValue))) {
			LOGGER.warn("{} haven't a good value", paramName);
			throw new NotAuthorizedException(paramName + " haven't a good value");
		}
	}

	/**
	 * Getting all client authorizations from HTTP method.
	 * 
	 * @param message
	 * @return CustomBasicAuth
	 */
	private CustomBasicAuth getBasicAuth(Message message) {
		String requestMethod = getRequestMethod(message);

		if (Constants.HTTP_GET.equalsIgnoreCase(requestMethod)) {
			return getAuth;
		} else if (Constants.HTTP_POST.equalsIgnoreCase(requestMethod)) {
			return postAuth;
		} else if (Constants.HTTP_PUT.equalsIgnoreCase(requestMethod)) {
			return putAuth;
		} else if (Constants.HTTP_DELETE.equalsIgnoreCase(requestMethod)) {
			return deleteAuth;
		}

		return null;
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
