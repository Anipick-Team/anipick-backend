package com.anipick.backend.oauth.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.domain.AppleDefaults;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppleVerifierProcessor {
    private final RestClient restClient = RestClient.create();
    // kid -> 공개키. Apple 키 교체 시 kid가 바뀌므로 없는 kid가 들어오면 다시 받아온다.
    private final Map<String, Key> publicKeys = new ConcurrentHashMap<>();

    @Value("${app.oauth2.apple.client-id}")
    private String clientId;

    public Claims verifyIdentityToken(String identityToken) {
        try {
            return Jwts.parser()
                    .keyLocator(new LocatorAdapter<Key>() {
                        @Override
                        protected Key locate(ProtectedHeader header) {
                            return findPublicKey(header.getKeyId());
                        }
                    })
                    .requireIssuer(AppleDefaults.DEFAULT_ISSUER)
                    .requireAudience(clientId)
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }

    private Key findPublicKey(String keyId) {
        if (keyId == null) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }

        Key key = publicKeys.get(keyId);
        if (key == null) {
            refreshPublicKeys();
            key = publicKeys.get(keyId);
        }

        if (key == null) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
        return key;
    }

    private synchronized void refreshPublicKeys() {
        JwkSet jwkSet = Jwks.setParser()
                .build()
                .parse(fetchPublicKeysJson());

        for (Jwk<?> jwk : jwkSet.getKeys()) {
            publicKeys.put(jwk.getId(), jwk.toKey());
        }
    }

    protected String fetchPublicKeysJson() {
        return restClient.get()
                .uri(AppleDefaults.DEFAULT_PUBLIC_KEYS_URL)
                .retrieve()
                .body(String.class);
    }
}
