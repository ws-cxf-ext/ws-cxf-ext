package org.ws.cxf.ext.utils;

/**
 * Created by ineumann on 1/7/19.
 */
public class CheckStatus {
    private boolean ok;

    private String message;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static CheckStatus newInstance() {
        return new CheckStatus();
    }

    public CheckStatus message(String message) {
        setMessage(message);
        return this;
    }

    public CheckStatus ok() {
        setOk(true);
        return this;
    }

    public CheckStatus ko() {
        setOk(false);
        return this;
    }
}
