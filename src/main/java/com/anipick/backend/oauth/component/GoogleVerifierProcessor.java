package com.anipick.backend.oauth.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class GoogleVerifierProcessor {
    @Value("${app.oauth2.google.android-client-id}")
    private String androidClientId;

    @Value("${app.oauth2.google.ios-client-id}")
    private String iosClientId;

    public GoogleIdToken.Payload verifyAndroidGoogleToken(String token) throws GeneralSecurityException, IOException {
        return getPayload(token, androidClientId);
    }

    public GoogleIdToken.Payload verifyIosGoogleToken(String token) throws GeneralSecurityException, IOException {
        return getPayload(token, iosClientId);
    }

    private static GoogleIdToken.Payload getPayload(String token, String clientId) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(token);
        if(googleIdToken != null) {
            return googleIdToken.getPayload();
        } else {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }
}
