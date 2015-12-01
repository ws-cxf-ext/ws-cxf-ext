package org.ws.cxf.ext.correlation;

/**
 * Local threads (correlation id, etc).
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CurrentCorrelationId {
	/**
	 * Correlation id.
	 */
	private static final ThreadLocal<String> CURRENT = new ThreadLocal<String>();

	/**
	 * Client.
	 */
	private static final ThreadLocal<String> CURRENT_CLIENT = new ThreadLocal<String>();

	/**
	 * Connected user login (from Spring security context).
	 */
	private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<String>();

	/**
	 * Getting the correlation id.
	 * 
	 * @return String
	 */
	public String getCurrentCorrelationId() {
		return CURRENT.get();
	}

	/**
	 * Getting the client.
	 * 
	 * @return String
	 */
	public String getCurrentClient() {
		return CURRENT_CLIENT.get();
	}

	/**
	 * Getting the user.
	 * 
	 * @return String
	 */
	public String getCurrentUser() {
		return CURRENT_USER.get();
	}

	/**
	 * Setting the current correlation id.
	 * 
	 * @param correlationId
	 */
	public void setCurrentCorrelationId(String correlationId) {
		CURRENT.set(correlationId);
	}

	/**
	 * Setting the client.
	 * 
	 * @param client
	 */
	public void setCurrentClient(String client) {
		CURRENT_CLIENT.set(client);
	}

	/**
	 * Setting the user.
	 * 
	 * @param user
	 */
	public void setCurrentUser(String user) {
		CURRENT_USER.set(user);
	}
}
