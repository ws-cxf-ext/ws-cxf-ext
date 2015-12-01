package org.ws.cxf.ext.utils.others;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.ws.cxf.ext.exception.FonctionalException;

/**
 * Récupération et traitement de l'exception renvoyée par le service REST dans
 * le header HTTP "Exception".
 * 
 * @author Christian Cougourdan <christian.cougourdan@capgemini.com>
 */
public class ResponseExceptionHandler implements ResponseExceptionMapper<FonctionalException> {

	@Override
	public FonctionalException fromResponse(Response r) {
		return new FonctionalException(r.getHeaderString("Exception"));
	}

}
