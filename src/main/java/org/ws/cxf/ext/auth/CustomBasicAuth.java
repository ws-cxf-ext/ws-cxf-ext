package org.ws.cxf.ext.auth;

import java.util.List;

/**
 * Custom Basic Auth.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class CustomBasicAuth {
    /**
     * PUT, POST, GET, DELETE
     */
    private String method;

    private List<String> appids;

    private List<String> exceptions;

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
    public List<String> getExceptions() {
        return exceptions;
    }

    /**
     * @param exceptions
     *            the exceptions to set
     */
    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }
}
