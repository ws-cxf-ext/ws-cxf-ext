package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Test for HTTP utils class.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 * @author Christian Cougourdan
 *
 */
public class HTTPUtilsTest {
	/**
	 * Test get query map where query is null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetQueryMapNull() {
		Map<String, String> params = HTTPUtils.getQueryMap(null);

		assertNotNull(params);
		assertTrue(params.isEmpty());
	}

	/**
	 * Test get query map where query is empty.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetQueryMapEmpty() {
		Map<String, String> params = HTTPUtils.getQueryMap("");

		assertNotNull(params);
		assertTrue(params.isEmpty());
	}

	/**
	 * Test get query map nominal.
	 */
	@Test
	public void testGetQueryMapNominal() {
		Map<String, String> params = HTTPUtils.getQueryMap("k1=v1&k2=v2&k3=v3");

		assertNotNull(params);
		assertEquals(3, params.size());
		assertEquals("v1", params.get("k1"));
		assertEquals("v2", params.get("k2"));
		assertEquals("v3", params.get("k3"));
	}

	/**
	 * Test query map with param without value.
	 */
	@Test
	public void testGetQueryMapWithoutValue() {
		Map<String, String> params = HTTPUtils.getQueryMap("k1=v1&k2=&k3=v3");

		assertNotNull(params);
		assertEquals(2, params.size());
		assertEquals("v1", params.get("k1"));
		assertNull(params.get("k2"));
		assertEquals("v3", params.get("k3"));
	}

	/**
	 * Test query map with param without assignation.
	 */
	@Test
	public void testGetQueryMapWithoutAssign() {
		Map<String, String> params = HTTPUtils.getQueryMap("k1=v1&k2&k3=v3");

		assertNotNull(params);
		assertEquals(2, params.size());
		assertEquals("v1", params.get("k1"));
		assertNull(params.get("k2"));
		assertEquals("v3", params.get("k3"));
	}

	/**
	 * Test query map with multiple assignation.
	 */
	@Test
	public void testGetQueryMapDoubleNamed() {
		Map<String, String> params = HTTPUtils.getQueryMap("k1=v1&k2=v2&k2=v3");

		assertNotNull(params);
		assertEquals(2, params.size());
		assertEquals("v1", params.get("k1"));
		assertEquals("v3", params.get("k2"));
	}

}
