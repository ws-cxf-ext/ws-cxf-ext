package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
