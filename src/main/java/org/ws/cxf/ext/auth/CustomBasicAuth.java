package org.ws.cxf.ext.auth;

import java.util.List;
import java.util.ArrayList;

/**
 * Custom Basic Auth.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CustomBasicAuth {
	/**
	 * PUT, POST, GET, DELETE
	 */
	private String method;

	private List<String> appids;

	private List<ExceptionAuth> exceptions;

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the appids
	 */
	public List<String> getAppids() {
		return appids;
	}

	/**
	 * @param appids
	 *            the appids to set
	 */
	public void setAppids(List<String> appids) {
		this.appids = appids;
	}

        /**
         * @return the exceptions
         */
        public ExceptionAuth getException() {
                if(null == exceptions) {
                    return null;
                }
                return exceptions.get(0);
        }

        /**
         * @param exceptions
         *            the exceptions to set
         */
        public void setException(ExceptionAuth exception) {
                this.exceptions = new ArrayList<>();
                this.exceptions.add(exception);
        }


	/**
	 * @return the exceptions
	 */
	public List<ExceptionAuth> getExceptions() {
		return exceptions;
	}

	/**
	 * @param exceptions
	 *            the exceptions to set
	 */
	public void setExceptions(List<ExceptionAuth> exceptions) {
		this.exceptions = exceptions;
	}
}