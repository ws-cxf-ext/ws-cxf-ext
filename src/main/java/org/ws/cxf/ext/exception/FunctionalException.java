package org.ws.cxf.ext.exception;

/**
 * Fonctionnal exception.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class FunctionalException extends Exception implements ICxfExtraException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FunctionalException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 * @param cause
	 */
	public FunctionalException(String code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 */
	public FunctionalException(String code) {
		super(code);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public FunctionalException(Throwable cause) {
		super(cause);
	}

}
