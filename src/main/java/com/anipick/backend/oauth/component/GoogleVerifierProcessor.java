package com.anipick.backend.oauth.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class GoogleVerifierProcessor {
    private final List<String> androidClientIds;

    private final List<String> iosClientIds;

    public GoogleVerifierProcessor(
            @Value("${app.oauth2.google.android-client-id}") String androidClientIds,
            @Value("${app.oauth2.google.ios-client-id}") String iosClientIds) {
        this.androidClientIds = parseClientIds(androidClientIds);
        this.iosClientIds = parseClientIds(iosClientIds);
    }

    public GoogleIdToken.Payload verifyAndroidGoogleToken(String token) throws GeneralSecurityException, IOException {
        return getPayload(token, androidClientIds);
    }

    public GoogleIdToken.Payload verifyIosGoogleToken(String token) throws GeneralSecurityException, IOException {
        return getPayload(token, iosClientIds);
    }

    private static List<String> parseClientIds(String raw) {
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .toList();
    }

    private static GoogleIdToken.Payload getPayload(String token, Collection<String> audience) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(audience)
                .build();

        GoogleIdToken googleIdToken = verifier.verify(token);
        if(googleIdToken != null) {
            return googleIdToken.getPayload();
        } else {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }
}
