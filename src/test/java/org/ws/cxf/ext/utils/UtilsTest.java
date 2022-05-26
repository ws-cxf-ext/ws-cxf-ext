package org.ws.cxf.ext.utils;

import org.junit.Test;
import org.ws.cxf.ext.appid.ICurrentAppId;
import org.ws.cxf.ext.auth.CustomBasicAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilsTest {
    @Test
    public final void testCheckSignature_disableAuth() {
        // Given
        String appid = "myappid";
        Map<String, String> hashByAppid = new HashMap<>();
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "whatever";
        String env = "dev";

        // When
        CheckStatus result = Utils.checkSignature(true, env, signature, service, Optional.empty(), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(true, result.isOk());
    }

    @Test
    public final void testCheckSignature_badSignature() {
        // Given
        String appid = "myappid";
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String signature = "badsignature";
        String service = "/v1/user/all?startIndex=0&maxResults=100";

        // When
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.empty(), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(false, result.isOk());
        assertEquals("auth_consumer_key haven't a good value", result.getMessage());
    }

    @Test
    public final void testCheckSignature_noAuth() {
        // Given
        String appid = "myappid";
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "Auth ?auth_signature=3mGHk9fOYOoEzP8PmQvpkcOk9ak%3D&auth_nonce=4Hoqb%2FqGABuVUcgevPnWFEG9hTc%3D&auth_callback=%2Fv1%2Fuser%2Fall%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652477329113&auth_token=fc228e62-18bd-4594-99d2-60470d505cc0&auth_signature_method=HMAC-SHA1&auth_consumer_key=rdSZyzfUWPVAyzXptT%2FNYDYzSgU%3D";

        // When
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.empty(), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(true, result.isOk());
    }

    @Test
    public final void testCheckSignature_noAppidOpened() {
        // Given
        String appid = "myappid";
        CustomBasicAuth auth = CustomBasicAuth.newInstance().method("GET").addAppid(appid);
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "Auth ?auth_signature=3mGHk9fOYOoEzP8PmQvpkcOk9ak%3D&auth_nonce=4Hoqb%2FqGABuVUcgevPnWFEG9hTc%3D&auth_callback=%2Fv1%2Fuser%2Fall%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652477329113&auth_token=fc228e62-18bd-4594-99d2-60470d505cc0&auth_signature_method=HMAC-SHA1&auth_consumer_key=rdSZyzfUWPVAyzXptT%2FNYDYzSgU%3D";

        // When
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.of(auth), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(true, result.isOk());
    }

    @Test
    public final void testCheckSignature_sameAppId() {
        // Given
        String appid = "myappid";
        CustomBasicAuth auth = CustomBasicAuth.newInstance().method("GET").addAppid(appid);
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "Auth ?auth_signature=3mGHk9fOYOoEzP8PmQvpkcOk9ak%3D&auth_nonce=4Hoqb%2FqGABuVUcgevPnWFEG9hTc%3D&auth_callback=%2Fv1%2Fuser%2Fall%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652477329113&auth_token=fc228e62-18bd-4594-99d2-60470d505cc0&auth_signature_method=HMAC-SHA1&auth_consumer_key=rdSZyzfUWPVAyzXptT%2FNYDYzSgU%3D";

        // When
        Utils.populateHashByAppid(auth, env, hashByAppid);
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.of(auth), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(true, result.isOk());
    }

    @Test
    public final void testCheckSignature_multipleAppIds() {
        // Given
        String appid = "myappid";
        CustomBasicAuth auth = CustomBasicAuth.newInstance().method("GET").addAppid(appid).addAppid("myappid2");
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "Auth ?auth_signature=3mGHk9fOYOoEzP8PmQvpkcOk9ak%3D&auth_nonce=4Hoqb%2FqGABuVUcgevPnWFEG9hTc%3D&auth_callback=%2Fv1%2Fuser%2Fall%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652477329113&auth_token=fc228e62-18bd-4594-99d2-60470d505cc0&auth_signature_method=HMAC-SHA1&auth_consumer_key=rdSZyzfUWPVAyzXptT%2FNYDYzSgU%3D";

        // When
        Utils.populateHashByAppid(auth, env, hashByAppid);
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.of(auth), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(true, result.isOk());
    }

    @Test
    public final void testCheckSignature_notTheSameAppId() {
        // Given
        String appid = "myappid";
        CustomBasicAuth auth = CustomBasicAuth.newInstance().method("GET").addAppid(appid);
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=100";
        String signature = "Auth ?auth_signature=ZX7KUqhyxIybyyRiGydCjlT%2F%2Fi8%3D&auth_nonce=zmoFyQWQ0kTRekARRXUHN6lV1lI%3D&auth_callback=%2Fv1%2Fusers%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652478341167&auth_token=9aab615c-cb83-40ba-b646-99e652a34b2a&auth_signature_method=HMAC-SHA1&auth_consumer_key=%2BAHyPRkzoxuQoaWWTx%2B9V5U6YHI%3D";

        // When
        Utils.populateHashByAppid(auth, env, hashByAppid);
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.of(auth), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(false, result.isOk());
        assertEquals("Unkown consumer +AHyPRkzoxuQoaWWTx+9V5U6YHI=", result.getMessage());
    }

    @Test
    public final void testCheckSignature_notTheSameArgsQueryParam() {
        // Given
        String appid = "myappid";
        CustomBasicAuth auth = CustomBasicAuth.newInstance().method("GET").addAppid(appid);
        Map<String, String> hashByAppid = new HashMap<>();
        String env = "dev";
        String service = "/v1/user/all?startIndex=0&maxResults=101";
        String signature = "Auth ?auth_signature=3mGHk9fOYOoEzP8PmQvpkcOk9ak%3D&auth_nonce=4Hoqb%2FqGABuVUcgevPnWFEG9hTc%3D&auth_callback=%2Fv1%2Fuser%2Fall%3FstartIndex%3D0%26maxResults%3D100&auth_timestamp=1652477329113&auth_token=fc228e62-18bd-4594-99d2-60470d505cc0&auth_signature_method=HMAC-SHA1&auth_consumer_key=rdSZyzfUWPVAyzXptT%2FNYDYzSgU%3D";

        // When
        Utils.populateHashByAppid(auth, env, hashByAppid);
        CheckStatus result = Utils.checkSignature(false, env, signature, service, Optional.of(auth), Optional.empty(), hashByAppid, new ICurrentAppId.Default());

        // Then
        assertNotNull(result);
        assertEquals(false, result.isOk());
        assertEquals("Unkown consumer rdSZyzfUWPVAyzXptT/NYDYzSgU=", result.getMessage());
    }
}
