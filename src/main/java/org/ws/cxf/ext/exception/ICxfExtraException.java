package org.ws.cxf.ext.exception;

public interface ICxfExtraException {
    String getMessage();

    Throwable getCause();
}
