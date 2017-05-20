package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.ws.cxf.ext.AbstractTest;

/**
 * Security utils test.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class SecurityUtilsTest extends AbstractTest {
	/**
	 * Nominal test.
	 */
	@Test
	public final void testGetSHA1HmacQuietlyNominal() {
		String key = "secret";
		String message = "msg";
		String hash = SecurityUtils.getSHA1Hmac(key, message);
		String hash2 = SecurityUtils.getSHA1Hmac(key, message);

		System.out.println("testGetSHA1HmacQuietlyNominal hash = " + hash);
		System.out.println("testGetSHA1HmacQuietlyNominal hash2 = " + hash2);

		assertTrue(SecurityUtils.isEquals(hash, hash2));
	}

	/**
	 * Failure test.
	 */
	@Test
	public final void testGetSHA1HmacQuietlyFail() {
		String key = "secret";
		String key2 = "secret2";
		String message = "msg";
		String hash = SecurityUtils.getSHA1Hmac(key, message);
		String hash2 = SecurityUtils.getSHA1Hmac(key2, message);

		System.out.println("testGetSHA1HmacQuietlyFail hash = " + hash);
		System.out.println("testGetSHA1HmacQuietlyFail hash2 = " + hash2);

		assertFalse(SecurityUtils.isEquals(hash, hash2));
	}

	/**
	 * Nominal test.
	 */
	@Test
	public final void testGetMD5HashQuietlyNominal() {
		String str = "test";
		String hash = SecurityUtils.getMD5HashQuietly(str);
		String hash2 = SecurityUtils.getMD5HashQuietly(str);

		assertTrue(SecurityUtils.isEquals(hash, hash2));
	}

	/**
	 * Fail test
	 */
	@Test
	public final void testGetMD5HashQuietlyFail() {
		String str = "test";
		String str2 = "tesssst2";
		String hash = SecurityUtils.getMD5HashQuietly(str);
		String hash2 = SecurityUtils.getMD5HashQuietly(str2);

		assertFalse(SecurityUtils.isEquals(hash, hash2));
	}

	/**
	 * Getting adress ip tests.
	 */
	@Test
	public final void testGetIPAdressQuietly() {
		String ip = SecurityUtils.getIPAdressQuietly();
		System.out.println("ip = " + ip);
		assertNotNull(ip);
	}

	/**
	 * Getting location infos tests.
	 */
	@Test
	public final void testGetClientLocalisationInfos() {
		Map<String, String> map = SecurityUtils.getClientLocalisationInfosQuietly("197.17.201.138");
		System.out.println("infos = " + JSONUtils.map2json(map));
		assertNotNull(map);
		assertTrue(MapUtils.isNotEmpty(map));
	}
}
