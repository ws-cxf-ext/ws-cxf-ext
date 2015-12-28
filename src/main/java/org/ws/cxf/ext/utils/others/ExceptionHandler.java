package org.ws.cxf.ext.utils.others;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handler.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public class ExceptionHandler implements ExceptionMapper<Exception> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response toResponse(Exception exception) {
		Integer code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

		if (exception instanceof ClientErrorException) {
			code = ((ClientErrorException) exception).getResponse().getStatus();
		}

		LOGGER.error("Here is an exception", exception);

		return Response.status(code) //
		        .header("Exception", exception.getMessage()) //
		        .header("ExceptionType", exception.getClass().getSimpleName()) //
		        .build();
	}

}
