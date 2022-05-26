package org.ws.cxf.ext.appid;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public interface ICurrentAppId {
    String getCurrentAppId();

    void setCurrentAppId(String appId);

    boolean isAdmin();

    class Default implements ICurrentAppId {
        @Override
        public String getCurrentAppId() {
            return EMPTY;
        }

        @Override
        public void setCurrentAppId(String appId) {
            // Nothing todo
        }

        @Override
        public boolean isAdmin() {
            return false;
        }
    }
}
