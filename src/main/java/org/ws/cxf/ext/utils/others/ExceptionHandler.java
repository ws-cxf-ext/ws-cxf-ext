package org.ws.cxf.ext.utils.others;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.ws.cxf.ext.exception.FonctionalException;

/**
 * Exception handler.
 * 
 * @author Christian Cougourdan
 */
public class ExceptionHandler implements ExceptionMapper<FonctionalException> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response toResponse(FonctionalException exception) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Exception", exception.getMessage()).build();
	}

}
