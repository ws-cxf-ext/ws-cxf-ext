package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.ws.cxf.ext.AbstractTest;

/**
 * JSONUtils tests.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class JSONUtilsTest extends AbstractTest {
	private final String JSON = "{\"key\":\"value\"}";
	private final String KEY = "key";
	private final String VALUE = "value";

	/**
	 * Test nominal map2json.
	 */
	@Test
	public final void map2jsonNominal() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY, VALUE);
		String res = JSONUtils.map2json(map);
		assertEquals(JSON, res);
	}

	/**
	 * Test empty map map2json.
	 */
	@Test
	public final void map2jsonEmpty() {
		Map<String, String> map = new HashMap<String, String>();
		String res = JSONUtils.map2json(map);
		assertEquals("", res);
	}

	/**
	 * Test nominal map2json.
	 */
	@Test
	public final void json2mapNominal() {
		Map<String, String> res = JSONUtils.json2map(JSON);
		assertNotNull(res);
		assertEquals(1, res.size());
		assertTrue(res.containsKey(KEY));
		assertEquals(VALUE, res.get(KEY));
	}

	/**
	 * Test empty map map2json.
	 */
	@Test
	public final void json2mapEmpty() {
		Map<String, String> res = JSONUtils.json2map("");
		assertNull(res);
	}

	/**
	 * Test nominal list2json.
	 */
	@Test
	public final void list2jsonNominal() {
		List<List<String>> test = new ArrayList<List<String>>();
		test.add(new ArrayList<String>());
		test.get(0).add("test");
		test.get(0).add("test2");
		test.add(new ArrayList<String>());
		test.get(1).add("test3");
		test.get(1).add("test4");
		assertEquals("[[\"test\",\"test2\"],[\"test3\",\"test4\"]]", JSONUtils.list2json(test));
	}
}
