package org.ws.cxf.ext.auth;

import java.util.List;

/**
 * Exception class which allow us to disable auth or change the list of appids
 * for a specific ws (or group of ws).
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class ExceptionAuth implements IAuth {
	/**
	 * Pattern wich match the webservice URI.
	 */
	private String pattern;

	/**
	 * Disable auth for the webservice pattern.
	 */
	private Boolean disable;

	/**
	 * List of specific appid for the pattern wich match the webservice URI.
	 */
	private List<String> appids;

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the disable
	 */
	public Boolean getDisable() {
		return disable;
	}

	/**
	 * @param disable
	 *            the disable to set
	 */
	public void setDisable(Boolean disable) {
		this.disable = disable;
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
}
