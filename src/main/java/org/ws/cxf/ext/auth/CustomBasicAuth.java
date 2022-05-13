package org.ws.cxf.ext.auth;

import java.util.List;
import java.util.ArrayList;

/**
 * Custom Basic Auth.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CustomBasicAuth implements IAuth {
	/**
	 * PUT, POST, GET, DELETE
	 */
	private String method;

	private List<String> appids;

	private List<ExceptionAuth> exceptions;

	private ExceptionAuth exception;

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
			return exception;
	}

	/**
	 * @param exceptions
	 *            the exceptions to set
	 */
	public void setException(ExceptionAuth exception) {
		this.exception = exception;
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

	public static CustomBasicAuth newInstance() {
		return new CustomBasicAuth();
	}

	public CustomBasicAuth method(String method) {
		setMethod(method);
		return this;
	}

	public CustomBasicAuth exception(ExceptionAuth exception) {
		setException(exception);
		return this;
	}

	public CustomBasicAuth exceptions(List<ExceptionAuth> exceptions) {
		setExceptions(exceptions);
		return this;
	}

	public CustomBasicAuth addException(ExceptionAuth exception) {
		if (null == getExceptions()) {
			exceptions(new ArrayList<>());
		}

		getExceptions().add(exception);
		return this;
	}

	public CustomBasicAuth appids(List<String> appids) {
		setAppids(appids);
		return this;
	}

	public CustomBasicAuth addAppid(String appid) {
		if (null == getAppids()) {
			setAppids(new ArrayList<>());
		}

		getAppids().add(appid);
		return this;
	}
}
