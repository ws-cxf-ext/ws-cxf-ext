package org.ws.cxf.ext.appid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.ws.cxf.ext.auth.CustomBasicAuth;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class CurrentAppId implements ICurrentAppId {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    @Autowired
    @Qualifier("authAdm")
    private CustomBasicAuth admAccess;

    @Override
    public String getCurrentAppId() {
        return CURRENT.get();
    }

    @Override
    public void setCurrentAppId(String appId) {
        CURRENT.set(appId);
    }

    @Override
    public boolean isAdmin() {
        return isNotEmpty(admAccess.getAppids()) && isNotBlank(CURRENT.get()) && admAccess.getAppids().stream().filter(a -> a.equalsIgnoreCase(CURRENT.get())).findAny().isPresent();
    }
}
