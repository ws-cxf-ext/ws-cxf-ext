package org.ws.cxf.ext.exception;

/**
 * Technical exception.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class TechnicalException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public TechnicalException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 * @param cause
	 */
	public TechnicalException(String code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 */
	public TechnicalException(String code) {
		super(code);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public TechnicalException(Throwable cause) {
		super(cause);
	}
}