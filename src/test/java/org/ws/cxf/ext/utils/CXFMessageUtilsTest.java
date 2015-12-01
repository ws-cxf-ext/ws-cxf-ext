package org.ws.cxf.ext.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
