package org.ws.cxf.ext.utils.others;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.ws.cxf.ext.exception.FonctionalException;

/**
 * Getting and process exception that is thrown by a RESTful webservice.
 * 
 * @author Christian Cougourdan
 */
public class ResponseExceptionHandler implements ResponseExceptionMapper<FonctionalException> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FonctionalException fromResponse(Response r) {
		return new FonctionalException(r.getHeaderString("Exception"));
	}
}
