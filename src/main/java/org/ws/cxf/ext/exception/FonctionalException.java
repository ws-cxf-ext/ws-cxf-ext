package org.ws.cxf.ext.exception;

/**
 * Fonctionnal exception.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class FonctionalException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FonctionalException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 * @param cause
	 */
	public FonctionalException(String code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 */
	public FonctionalException(String code) {
		super(code);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public FonctionalException(Throwable cause) {
		super(cause);
	}

}
