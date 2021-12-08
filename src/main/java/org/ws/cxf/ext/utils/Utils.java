package org.ws.cxf.ext.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ws.cxf.ext.Constants;
import org.ws.cxf.ext.auth.CustomBasicAuth;
import org.ws.cxf.ext.auth.ExceptionAuth;
import org.ws.cxf.ext.auth.IAuth;

import javax.ws.rs.NotAuthorizedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ws.cxf.ext.Constants.CHARSET_UTF8;
import static org.ws.cxf.ext.utils.CXFMessageUtils.getRequestMethod;
import static org.ws.cxf.ext.utils.HTTPUtils.getQueryMap;
import static org.ws.cxf.ext.utils.HTTPUtils.httpBuildQuery;
import static org.ws.cxf.ext.utils.SecurityUtils.generateAuthParameters;
import static org.ws.cxf.ext.utils.SecurityUtils.getSHA1Hmac;
import static org.ws.cxf.ext.utils.SecurityUtils.isEquals;

/**
 * Created by ineumann on 1/7/19.
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * Auth token template
     */
    private static final String AUTH_TOKEN_TPL = "Auth %s";

    public static Optional<ExceptionAuth> getServiceException(Optional<CustomBasicAuth> auth, String service) {
        if(!auth.isPresent()) {
            return Optional.empty();
        }

        List<ExceptionAuth> exs = auth.get().getExceptions();
        if(CollectionUtils.isEmpty(exs)) {
            exs = new ArrayList<>();
        }

        if(null != auth.get().getException()) {
            exs.add(auth.get().getException());
        }

        if (isBlank(service) || CollectionUtils.isEmpty(exs)) {
            return Optional.empty();
        }

        return exs.stream().filter(e -> service.toLowerCase().contains(e.getPattern().toLowerCase())).findFirst();
    }

    public static String generateSignature(String appid, String env, String uri) {
        return String.format(AUTH_TOKEN_TPL, httpBuildQuery(generateAuthParameters(appid, env, uri)));
    }

    public static CheckStatus checkAuthParam(String paramName, String paramValue, String expectedValue) {
        if (null == paramValue || (null != expectedValue && !expectedValue.equalsIgnoreCase(paramValue))) {
            return CheckStatus.newInstance().ko().message(paramName + " haven't a good value");
        }

        return CheckStatus.newInstance().ok();
    }

    public static CheckStatus checkAuthParam(String paramName, String paramValue) {
        return checkAuthParam(paramName, paramValue, null);
    }

    public static Optional<CustomBasicAuth> getBasicAuth(Message message, CustomBasicAuth getAuth, CustomBasicAuth postAuth, CustomBasicAuth putAuth, CustomBasicAuth deleteAuth) {
        String requestMethod = getRequestMethod(message);

        if (Constants.HTTP_GET.equalsIgnoreCase(requestMethod)) {
            return Optional.ofNullable(getAuth);
        } else if (Constants.HTTP_POST.equalsIgnoreCase(requestMethod)) {
            return Optional.ofNullable(postAuth);
        } else if (Constants.HTTP_PUT.equalsIgnoreCase(requestMethod)) {
            return Optional.ofNullable(putAuth);
        } else if (Constants.HTTP_DELETE.equalsIgnoreCase(requestMethod)) {
            return Optional.ofNullable(deleteAuth);
        }

        return Optional.empty();
    }

    public static String getHashFromAppid(String appid, String env, Map<String, String> hashByAppid) {
        if (null == hashByAppid) {
            hashByAppid = new HashMap<>();
        }

        if (!hashByAppid.containsKey(appid)) {
            hashByAppid.put(appid, getSHA1Hmac(appid, env));
        }

        return hashByAppid.get(appid);
    }

    public static void populateHashByAppid(CustomBasicAuth auth, String env, Map<String, String> hashByAppid) {
        if (CollectionUtils.isEmpty(auth.getAppids())) {
            return;
        }

        auth.getAppids().stream().filter(appid -> !hashByAppid.containsKey(appid)).forEach(appid -> hashByAppid.put(appid, getHashFromAppid(appid, env, hashByAppid)));
    }

    public static Predicate<String> isExpectedAuth(String env, String service, String hashConsumerKey, String hashSignature, Map<String, String> hashByAppid) {
        return appid -> {
            String hashConsumerKeyExpected = getHashFromAppid(appid, env, hashByAppid);
            String hashSignatureExpected = getSHA1Hmac(appid, service);
            return isEquals(hashConsumerKey, hashConsumerKeyExpected) && isEquals(hashSignature, hashSignatureExpected);
        };
    }

    public static List<String> lstAppids(Optional<? extends IAuth> o1, Optional<? extends IAuth> o2) {
        return o1.map(o -> (IAuth) o).orElse(o2.map(o -> (IAuth) o).orElse(new IAuth.Default())).getAppids();
    }

    public static CheckStatus checkSignature(boolean disableAuthParam, String env, String authorization, String service, Optional<CustomBasicAuth> auth, Optional<ExceptionAuth> exp, Map<String, String> hashByAppid) {
        if (disableAuthParam || (exp.isPresent() && null != exp.get().getDisable() && exp.get().getDisable())) {
            LOGGER.info("Forced security for the service : {}", service);
            return CheckStatus.newInstance().ok();
        }

        if (isEmpty(authorization)) {
            return CheckStatus.newInstance().ko().message("Missing authentication information for the service : " + service);
        }

        Map<String, String> authParams = getQueryMap(authorization);
        String hashConsumerKey = null;
        String hashSignature = null;
        String tokenDecode = null;

        // Parameter checking
        String consumerKey = authParams.get("auth_consumer_key");
        String signature = authParams.get("auth_signature");
        String token = authParams.get("auth_token");

        CheckStatus tmp = checkAuthParam("auth_consumer_key", consumerKey);
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_token", token);
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_callback", authParams.get("auth_callback"));
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_signature", signature);
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_nonce", authParams.get("auth_nonce"));
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_timestamp", authParams.get("auth_timestamp"));
        if(!tmp.isOk()) {
            return tmp;
        }

        tmp = checkAuthParam("auth_signature_method", authParams.get("auth_signature_method"), "HMAC-SHA1");
        if(!tmp.isOk()) {
            return tmp;
        }

        try {
            hashConsumerKey = URLDecoder.decode(consumerKey, CHARSET_UTF8);
            hashSignature = URLDecoder.decode(signature, CHARSET_UTF8);
            tokenDecode = URLDecoder.decode(token, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            return CheckStatus.newInstance().ko().message("UnsupportedEncodingException " + e.getMessage());
        }

        if (auth.isPresent()) {
            boolean needErr401 = true;

            List<String> lstAppids = lstAppids(exp, auth);
            if (null != hashConsumerKey && isNotEmpty(lstAppids)) {
                needErr401 = !lstAppids.stream().anyMatch(isExpectedAuth(env, service + tokenDecode, hashConsumerKey, hashSignature, hashByAppid));
            }

            if (needErr401) {
                return CheckStatus.newInstance().ko().message("Unkown consumer " + hashConsumerKey);
            }
        }

        return CheckStatus.newInstance().ok();
    }
}
