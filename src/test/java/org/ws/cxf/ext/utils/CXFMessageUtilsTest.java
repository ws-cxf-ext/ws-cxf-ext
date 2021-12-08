package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ws.cxf.ext.utils.CXFMessageUtils.keepOnlyWebserviceRequest;

import org.apache.cxf.phase.Phase;
import org.junit.Test;
import org.ws.cxf.ext.AbstractTest;

/**
 * CXFMessageUtils tests.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CXFMessageUtilsTest extends AbstractTest {
	/**
	 * Test keeping only the webservice requests
	 */
	@Test
	public final void testKeepOnlyWebserviceRequestNominal() {
		assertEquals("/v1/service?foo=bar", keepOnlyWebserviceRequest("http://127.0.0.1:8080/subpath/api/v1/service?foo=bar", "/subpath/api"));
		assertEquals("/v1/service?foo=bar", keepOnlyWebserviceRequest("https://api.mydomain.io/api/v1/service?foo=bar", "/api"));
		assertEquals("/v1/service?foo=bar", keepOnlyWebserviceRequest("https://api.mydomain.io/v1/service?foo=bar", "/api"));
	}

	/**
	 * Test nominal isPhaseOutbound.
	 */
	@Test
	public final void testIsPhaseOutboundNominal() {
		String phase = Phase.SETUP;
		assertTrue(CXFMessageUtils.isPhaseOutbound(phase));
	}

	/**
	 * Test failure isPhaseOutbound.
	 */
	@Test
	public final void testIsPhaseOutboundEchec() {
		String phase = Phase.RECEIVE;
		assertFalse(CXFMessageUtils.isPhaseOutbound(phase));
	}

	/**
	 * Test nominal isPhaseInbound.
	 */
	@Test
	public final void testIsPhaseInboundNominal() {
		String phase = Phase.RECEIVE;
		assertTrue(CXFMessageUtils.isPhaseInbound(phase));
	}

	/**
	 * Test failure isPhaseInbound.
	 */
	@Test
	public final void testIsPhaseInboundEchec() {
		String phase = Phase.SETUP;
		assertFalse(CXFMessageUtils.isPhaseInbound(phase));
	}
}
