package org.ws.cxf.ext.correlation;

import org.apache.logging.log4j.ThreadContext;

/**
 * Local threads (correlation id, etc).
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CurrentCorrelationId {
	/**
	 * Id de corrélation
	 */
	private static final ThreadLocal<String> CURRENT = new ThreadLocal<String>();

	/**
	 * Appelant (contextRoot dans le cas des mapi)
	 */
	private static final ThreadLocal<String> CURRENT_CLIENT = new ThreadLocal<String>();

	/**
	 * Login utilisateur connecté
	 */
	private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<String>();

	/**
	 * Récupérer l'id de corrélation du thread local
	 * 
	 * @return String
	 */
	public String getCurrentCorrelationId() {
		return CURRENT.get();
	}

	/**
	 * Récupérer le client
	 * 
	 * @return String
	 */
	public String getCurrentClient() {
		return CURRENT_CLIENT.get();
	}

	/**
	 * Récupérer l'utilisateur
	 * 
	 * @return String
	 */
	public String getCurrentUser() {
		return CURRENT_USER.get();
	}

	/**
	 * Valoriser l'id de corrélation du thread local
	 * 
	 * @param correlationId
	 */
	public void setCurrentCorrelationId(String correlationId) {
		CURRENT.set(correlationId);
		ThreadContext.put("correlationId", correlationId);
	}

	/**
	 * Valoriser l'id de l'appelant du thread local
	 * 
	 * @param client
	 */
	public void setCurrentClient(String client) {
		CURRENT_CLIENT.set(client);
		ThreadContext.put("client", client);
	}

	/**
	 * Valoriser l'id de l'utilisateur connecté du thread local (Utilisé dans
	 * les mas)
	 * 
	 * @param user
	 */
	public void setCurrentUser(String user) {
		CURRENT_USER.set(user);
		ThreadContext.put("user", user);
	}
}
