package org.ws.cxf.ext.interceptor;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ws.cxf.ext.Constants.CHARSET_UTF8;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAuthorizedException;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.ws.cxf.ext.Constants;
import org.ws.cxf.ext.auth.CustomBasicAuth;
import org.ws.cxf.ext.auth.ExceptionAuth;
import org.ws.cxf.ext.auth.IAuth;
import org.ws.cxf.ext.auth.IAuth.Default;

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

		auth.getAppids().stream().filter(appid -> !hashByAppid.containsKey(appid)).forEach(appid -> hashByAppid.put(appid, getHashFromAppid(appid)));
	}

	/**
	 * Initialization of bean after IoC.
	 */
	@PostConstruct
	public void init() {
		hashByAppid = new HashMap<>();
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
			hashByAppid = new HashMap<>();
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
	 * Return an exception for a webservice if exists.
	 *
	 */
	private Optional<ExceptionAuth> getServiceException(Optional<CustomBasicAuth> auth, String service) {
		if(!auth.isPresent()) {
			return Optional.empty();
		}

		List<ExceptionAuth> exs = auth.get().getExceptions();
		if(isEmpty(exs)) {
			exs = new ArrayList<>();
		}

		if(null != auth.get().getException()) {
			exs.add(auth.get().getException());
		}

		if (isBlank(service) || isEmpty(exs)) {
			return Optional.empty();
		}

		return exs.stream().filter(e -> service.toLowerCase().contains(e.getPattern().toLowerCase())).findFirst();
	}

	/**
	 * Checking authorization.
	 * 
	 * @param message
	 */
	private void checkAuth(Message message) {
		String authorization = getHeaderParam(message, "Authorization");

		String service = getRequestURI(message, true);
		Optional<CustomBasicAuth> auth = getBasicAuth(message);
		Optional<ExceptionAuth> exp = getServiceException(auth, service);

		if (disableAuthParam || (exp.isPresent() && null != exp.get().getDisable() && exp.get().getDisable())) {
			LOGGER.warn("Forced security for the service : {}", service);
			return;
		}

		if (isEmpty(authorization)) {
			LOGGER.warn("Missing authentication information for the service : {}", service);
			throw new NotAuthorizedException("Authentication error");
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
			hashConsumerKey = URLDecoder.decode(consumerKey, CHARSET_UTF8);
			hashSignature = URLDecoder.decode(signature, CHARSET_UTF8);
			tokenDecode = URLDecoder.decode(token, CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new NotAuthorizedException("Authentication error : UnsupportedEncodingException " + e.getMessage(), e);
		}

		if (auth.isPresent()) {
			boolean needErr401 = true;

			List<String> lstAppids = lstAppids(exp, auth);
			if (null != hashConsumerKey && isNotEmpty(lstAppids)) {
				needErr401 = !lstAppids.stream().anyMatch(isExpectedAuth(service + tokenDecode, hashConsumerKey, hashSignature));
			}

			if (needErr401) {
				LOGGER.warn("Unknown consumer : " + hashConsumerKey);
				throw new NotAuthorizedException("Authentication error");
			}
		}
	}

	public List<String> lstAppids(Optional<? extends IAuth> o1, Optional<? extends IAuth> o2) {
		return o1.map(o -> (IAuth) o).orElse(o2.map(o -> (IAuth) o).orElse(new IAuth.Default())).getAppids();
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
	 */
	private Optional<CustomBasicAuth> getBasicAuth(Message message) {
		String requestMethod = getRequestMethod(message);

		if (Constants.HTTP_GET.equalsIgnoreCase(requestMethod)) {
			return Optional.ofNullable(getAuth);
		} else if (Constants.HTTP_POST.equalsIgnoreCase(requestMethod)) {
			return Optional.ofNullable(postAuth);
		} else if (Constants.HTTP_PUT.equalsIgnoreCase(requestMethod)) {
			return Optional.ofNullable(putAuth);
		} else if (Constants.HTTP_DELETE.equalsIgnoreCase(requestMethod)) {
			return Optional.ofNullable(deleteAuth);
		}

		return Optional.empty();
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
