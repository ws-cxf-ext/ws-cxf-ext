package org.ws.cxf.ext.utils;

import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.ws.cxf.ext.utils.DateUtils.getCurrentTime;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.ws.cxf.ext.exception.CxfExtraTechnicalException;

/**
 * Utils class for security.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class SecurityUtils {

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

}
