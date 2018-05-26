package org.ws.cxf.ext.auth;

import java.util.ArrayList;
import java.util.List;

public interface IAuth {
    List<String> getAppids();

    class Default implements IAuth {
        public List<String> getAppids() {
            return new ArrayList<String>();
        }
    }
}
