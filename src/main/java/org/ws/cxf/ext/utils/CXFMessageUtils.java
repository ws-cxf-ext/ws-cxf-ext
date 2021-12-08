package org.ws.cxf.ext.utils;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CXF message parsing utils.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CXFMessageUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CXFMessageUtils.class);

	/**
	 * Getting the URI request.
	 * 
	 * @param message
	 * @param addHost
	 * @return String
	 */
	public static String getRequestURI(Message message, boolean addHost) {
		String queryString = getQueryString(message);
		String requestURI = (String) message.get(Message.REQUEST_URI);
		String host = (!addHost) ? "" : getHeaderParam(message, "X-Forwarded-Proto") + "://" + getHeaderParam(message, "host");

		return host + requestURI + ((isEmpty(queryString)) ? "" : "?" + queryString);
	}

	/**
	 * Getting GET params.
	 * 
	 * @param message
	 * @return String
	 */
	public static String getQueryString(Message message) {
		return (String) message.get(Message.QUERY_STRING);
	}

	/**
	 * Getting HTTP body.
	 * 
	 * @param message
	 * @return String
	 */
	public static String getBodyQuietly(Message message) {
		try {
			InputStream is = message.getContent(InputStream.class);
			return (null == is) ? null : IOUtils.toString(is);
		} catch (IOException e) {
			LOGGER.warn("Error when reading body", e);
			return null;
		}
	}

	/**
	 * Getting headers params.
	 * 
	 * @param message
	 * @return Map<String, List<String>>
	 */
	public static Map<String, List<String>> getQueryHeader(Message message) {
		return CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
	}

	/**
	 * Getting query string quietly.
	 * 
	 * @param message
	 * @return String
	 */
	public static String getQueryStringQuietly(Message message) {
		String queryString = getQueryString(message);
		return isEmpty(queryString) ? "" : queryString;
	}

	/**
	 * Adding header param.
	 * 
	 * @param message
	 * @param paramName
	 * @param paramValue
	 */
	public static void addHeaderParam(Message message, String paramName, String paramValue) {
		Map<String, List<String>> headers = getQueryHeader(message);
		headers.put(paramName, Collections.singletonList(paramValue));
	}

	/**
	 * Getting param header
	 * 
	 * @param message
	 * @param paramName
	 * @return String
	 */
	public static String getHeaderParam(Message message, String paramName) {
		Map<String, List<String>> headers = getQueryHeader(message);
		List<?> sa = (headers == null) ? null : headers.get(paramName);
		return (isEmpty(sa)) ? null : (String) sa.get(0);
	}

	/**
	 * Getting HTTP method (GET, PUT, POST, DELETE).
	 * 
	 * @param message
	 * @return String
	 */
	public static String getRequestMethod(Message message) {
		return (String) message.get(Message.HTTP_REQUEST_METHOD);
	}

	/**
	 * Getting HTTP return code.
	 * 
	 * @param message
	 * @return String
	 */
	public static Integer getRequestReturnCode(Message message) {
		return (Integer) message.get(Message.RESPONSE_CODE);
	}

	/**
	 * Getting HTTP servlet request.
	 * 
	 * @param message
	 * @return ServletRequest
	 */
	public static ServletRequest getHTTPRequest(Message message) {
		return (ServletRequest) message.get("HTTP.REQUEST");
	}

	/**
	 * Getting client IP or domain adress.
	 * 
	 * @param message
	 * @return String
	 */
	public static String getRemoteAddrQuietly(Message message) {
		ServletRequest r = getHTTPRequest(message);
		return (null == r) ? null : r.getRemoteAddr();
	}

	/**
	 * Getting correlation id.
	 * 
	 * @param message
	 * @param defaultCorrelationId
	 * @return String
	 */
	public static String getCorrelationId(Message message, String defaultCorrelationId) {
		return calcValue("RequestId", message, UUID.randomUUID().toString(), defaultCorrelationId);
	}

	/**
	 * Getting client name.
	 * 
	 * @param message
	 * @param defaultClient
	 * @return String
	 */
	public static String getClient(Message message, String defaultClient) {
		return calcValue("ClientId", message, "unknown", defaultClient);
	}

	/**
	 * Getting value from headers and push it into local thread.
	 * 
	 * @param key
	 * @param message
	 * @param calcValue
	 * @param defaultValue
	 */
	public static String calcValue(String key, Message message, String calcValue, String defaultValue) {
		String correlationId = (String) message.getExchange().get(key);
		if (correlationId == null) {
			correlationId = (null == defaultValue) ? calcValue : defaultValue;
			message.getExchange().put(key, correlationId);
		}

		return correlationId;
	}

	/**
	 * Return true if is an incoming phase.
	 * 
	 * @param phase
	 * @return true|false
	 */
	public static boolean isPhaseInbound(String phase) {
		List<String> lstPhases = new ArrayList<String>();
		lstPhases.add(Phase.RECEIVE);
		lstPhases.add(Phase.PRE_STREAM);
		lstPhases.add(Phase.USER_STREAM);
		lstPhases.add(Phase.POST_STREAM);
		lstPhases.add(Phase.READ);
		lstPhases.add(Phase.UNMARSHAL);
		lstPhases.add(Phase.PRE_LOGICAL);
		lstPhases.add(Phase.USER_LOGICAL);
		lstPhases.add(Phase.POST_LOGICAL);
		lstPhases.add(Phase.PRE_INVOKE);
		lstPhases.add(Phase.INVOKE);
		lstPhases.add(Phase.POST_INVOKE);
		return lstPhases.contains(phase.toLowerCase());
	}

	/**
	 * Return true if is an outgoing phase.
	 * 
	 * @param phase
	 * @return true|false
	 */
	public static boolean isPhaseOutbound(String phase) {
		List<String> lstPhases = new ArrayList<String>();
		lstPhases.add(Phase.SETUP);
		lstPhases.add(Phase.PRE_LOGICAL);
		lstPhases.add(Phase.USER_LOGICAL);
		lstPhases.add(Phase.POST_LOGICAL);
		lstPhases.add(Phase.PREPARE_SEND);
		lstPhases.add(Phase.UNMARSHAL);
		lstPhases.add(Phase.PRE_STREAM);
		lstPhases.add(Phase.PRE_PROTOCOL);
		lstPhases.add(Phase.WRITE);
		lstPhases.add(Phase.PRE_MARSHAL);
		lstPhases.add(Phase.MARSHAL);
		lstPhases.add(Phase.POST_MARSHAL);
		lstPhases.add(Phase.USER_PROTOCOL);
		lstPhases.add(Phase.POST_PROTOCOL);
		lstPhases.add(Phase.USER_STREAM);
		lstPhases.add(Phase.POST_STREAM);
		lstPhases.add(Phase.SEND);
		return lstPhases.contains(phase.toLowerCase());
	}

	/**
	 * Utils class : private constructor.
	 */
	private CXFMessageUtils() {
	}
}
