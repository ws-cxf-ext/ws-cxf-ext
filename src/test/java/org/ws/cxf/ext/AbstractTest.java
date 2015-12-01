package org.ws.cxf.ext;

import static org.junit.Assert.fail;

import java.io.File;

/**
 * Abstract class for tests.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public abstract class AbstractTest {
	/**
	 * Failing with exception.
	 * 
	 * @param Exception
	 *            e
	 */
	public void failWithException(Exception e) {
		e.printStackTrace();
		fail("Exception inattendue : " + e.getMessage());
	}

	/**
	 * Getting test data dir.
	 * 
	 * @return String
	 */
	public String getTestDataDir() {
		return "src" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "test" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "resources" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "data" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + this.getClass().getSimpleName() // $NON-NLS-1$
		        + File.separator;
	}

	/**
	 * Getting common data dir.
	 * 
	 * @return String
	 */
	public String getTestCommonDataDir() {
		return "src" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "test" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "resources" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "data" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "Common" // $NON-NLS-1$
		        + File.separator;
	}

	/**
	 * Getting test resources data dir.
	 * 
	 * @return String
	 */
	public String getTestResourcesDir() {
		return "src" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "test" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "resources" // $NON-NLS-1$
		        + File.separator;
	}

	/**
	 * Getting main resources data dir.
	 * 
	 * @return String
	 */
	public String getDataDir() {
		return "src" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "main" // $NON-NLS-1$
		        + File.separator // $NON-NLS-1$
		        + "resources" // $NON-NLS-1$
		        + File.separator;
	}
}
