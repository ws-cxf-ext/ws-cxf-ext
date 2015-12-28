package org.ws.cxf.ext.utils.others;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.ws.cxf.ext.exception.FonctionalException;
import org.ws.cxf.ext.exception.TechnicalException;

/**
 * Getting and process exception that is thrown by a RESTful webservice.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class ResponseExceptionHandler implements ResponseExceptionMapper<Exception> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Exception fromResponse(Response r) {
		String typeOfException = r.getHeaderString("ExceptionType");
		String message = r.getHeaderString("Exception");

		if (FonctionalException.class.getSimpleName().equalsIgnoreCase(typeOfException)) {
			return new FonctionalException(message);
		} else if (BadRequestException.class.getSimpleName().equalsIgnoreCase(typeOfException)) {
			return new BadRequestException(message);
		} else if (NotFoundException.class.getSimpleName().equalsIgnoreCase(typeOfException)) {
			return new NotFoundException(message);
		} else {
			return new TechnicalException(message);
		}

	}
}
