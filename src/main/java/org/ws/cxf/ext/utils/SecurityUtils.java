package org.ws.cxf.ext.utils;

import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ws.cxf.ext.utils.DateUtils.getCurrentTime;
import static org.ws.cxf.ext.utils.HTTPUtils.httpGet;
import static org.ws.cxf.ext.utils.JSONUtils.json2mapQuietly;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ws.cxf.ext.exception.CxfExtraTechnicalException;

/**
 * Utils class for security.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class SecurityUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

	/**
	 * Utils class => private constructor.
	 */
	private SecurityUtils() {

	}

	/**
	 * Generating sha1-hmac hash.
	 * 
	 * @param privateKey
	 * @param input
	 * @return String
	 */
	public static String getSHA1Hmac(String privateKey, String input) {
		String algorithm = "HmacSHA1";
		byte[] keyBytes = privateKey.getBytes();
		Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
		Mac mac;

		try {
			mac = Mac.getInstance(algorithm);
			mac.init(key);
			return new String(encodeBase64(mac.doFinal(input.getBytes())));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CxfExtraTechnicalException(e);
		}
	}

	/**
	 * Generating md5 hash.
	 * 
	 * @param str
	 * @return String
	 */
	public static String getMD5HashQuietly(String str) {
		StringBuffer hexString = new StringBuffer();
		MessageDigest md;
		try {
			md = (MessageDigest) MessageDigest.getInstance("MD5").clone();

			byte[] hash = md.digest(str.getBytes());

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException | CloneNotSupportedException e) {
			LOGGER.error("Exception", e);
		}

		return null;
	}

	/**
	 * Compare two hash.
	 * 
	 * @param hash1
	 * @param hash2
	 * @return String
	 */
	public static boolean isEquals(String hash1, String hash2) {
		return MessageDigest.isEqual(hash1.getBytes(), hash2.getBytes());
	}

	/**
	 * Generate auth parameters.
	 * 
	 * @param appid
	 * @param env
	 * @param uri
	 * @return Map<String, String>
	 */
	public static Map<String, String> generateAuthParameters(String appid, String env, String uri) {
		Map<String, String> authAutorize = new HashMap<String, String>();

		String token = UUID.randomUUID().toString();
		authAutorize.put("auth_consumer_key", getSHA1Hmac(appid, env));
		authAutorize.put("auth_callback", uri);
		authAutorize.put("auth_nonce", getSHA1Hmac(appid, UUID.randomUUID().toString()));
		authAutorize.put("auth_signature", getSHA1Hmac(appid, uri + token));
		authAutorize.put("auth_signature_method", "HMAC-SHA1");
		authAutorize.put("auth_timestamp", String.valueOf(getCurrentTime().getTimeInMillis()));
		authAutorize.put("auth_token", token);

		return authAutorize;
	}

	/**
	 * Getting ip quietly.
	 * 
	 * @return String l'ip
	 */
	public static String getIPAdressQuietly() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error("[getIPAdressQuietly] error : " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Getting ip from request.
	 * 
	 * @return ip
	 */
	public static String getIPAdressQuietly(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		return ipAddress;
	}

	/**
	 * Getting IP from request.
	 * 
	 * @return ip
	 */
	public static String getIPAdressQuietly(ServletRequest request) {
		return request.getRemoteAddr();
	}

	/**
	 * Getting client infos.
	 * 
	 * @return Map<String, String>.
	 */
	public static Map<String, String> getClientLocalisationInfosQuietly(String ip) {
		String ws = "http://ipinfo.io/%s/json";

		String url = String.format(ws, ip);
		String json = null;

		try {
			json = httpGet(url);
		} catch (CxfExtraTechnicalException e) {
			LOGGER.error("Exception", e);
			return null;
		}

		if (isEmpty(json)) {
                    return null;
		}

		return json2mapQuietly(json);
	}

	/**
	 * Generate token.
	 * 
	 * @return String
	 */
	public static String generateToken() {
		String uuid = UUID.randomUUID().toString();
		return getMD5HashQuietly(uuid);
	}

}
