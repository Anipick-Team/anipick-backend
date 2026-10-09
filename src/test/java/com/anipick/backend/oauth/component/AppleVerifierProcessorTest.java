package com.anipick.backend.oauth.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppleVerifierProcessorTest {
    private static final String CLIENT_ID = "com.anipick.app";
    private static final String KEY_ID = "test-kid";

    private KeyPair keyPair;
    private int fetchCount;
    private AppleVerifierProcessor sut;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        fetchCount = 0;

        String jwks = toJwks((RSAPublicKey) keyPair.getPublic());
        sut = new AppleVerifierProcessor() {
            @Override
            protected String fetchPublicKeysJson() {
                fetchCount++;
                return jwks;
            }
        };
        ReflectionTestUtils.setField(sut, "clientId", CLIENT_ID);
    }

    @Test
    @DisplayName("유효한 identityToken이면 이메일 claim을 반환하고 공개키는 캐싱된다")
    void verifyIdentityToken_success() {
        String token = createToken(KEY_ID, keyPair.getPrivate(), "https://appleid.apple.com", CLIENT_ID, Instant.now().plusSeconds(600));

        Claims first = sut.verifyIdentityToken(token);
        sut.verifyIdentityToken(token);

        assertThat(first.get("email", String.class)).isEqualTo("user@privaterelay.appleid.com");
        assertThat(first.getSubject()).isEqualTo("apple-sub");
        assertThat(fetchCount).isEqualTo(1);
    }

    @Test
    @DisplayName("다른 앱(audience)용 토큰이면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_wrongAudience() {
        String token = createToken(KEY_ID, keyPair.getPrivate(), "https://appleid.apple.com", "com.other.app", Instant.now().plusSeconds(600));

        assertInvalid(token);
    }

    @Test
    @DisplayName("발급자가 Apple이 아니면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_wrongIssuer() {
        String token = createToken(KEY_ID, keyPair.getPrivate(), "https://evil.example.com", CLIENT_ID, Instant.now().plusSeconds(600));

        assertInvalid(token);
    }

    @Test
    @DisplayName("만료된 토큰이면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_expired() {
        String token = createToken(KEY_ID, keyPair.getPrivate(), "https://appleid.apple.com", CLIENT_ID, Instant.now().minusSeconds(600));

        assertInvalid(token);
    }

    @Test
    @DisplayName("Apple 키가 아닌 키로 서명된 토큰이면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_forgedSignature() throws Exception {
        PrivateKey otherKey = KeyPairGenerator.getInstance("RSA").generateKeyPair().getPrivate();
        String token = createToken(KEY_ID, otherKey, "https://appleid.apple.com", CLIENT_ID, Instant.now().plusSeconds(600));

        assertInvalid(token);
    }

    @Test
    @DisplayName("알 수 없는 kid면 공개키를 다시 받아오고, 그래도 없으면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_unknownKid() {
        String token = createToken("unknown-kid", keyPair.getPrivate(), "https://appleid.apple.com", CLIENT_ID, Instant.now().plusSeconds(600));

        assertInvalid(token);
        assertThat(fetchCount).isEqualTo(1);
    }

    @Test
    @DisplayName("JWT 형식이 아니면 REQUESTED_TOKEN_INVALID")
    void verifyIdentityToken_malformed() {
        assertInvalid("not.a.jwt");
    }

    private void assertInvalid(String token) {
        assertThatThrownBy(() -> sut.verifyIdentityToken(token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUESTED_TOKEN_INVALID);
    }

    private static String createToken(String kid, PrivateKey privateKey, String issuer, String audience, Instant expiresAt) {
        return Jwts.builder()
                .header().keyId(kid).and()
                .issuer(issuer)
                .audience().add(audience).and()
                .subject("apple-sub")
                .claim("email", "user@privaterelay.appleid.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(1200)))
                .expiration(Date.from(expiresAt))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private static String toJwks(RSAPublicKey publicKey) {
        return """
                {"keys":[{"kty":"RSA","kid":"%s","use":"sig","alg":"RS256","n":"%s","e":"%s"}]}
                """.formatted(KEY_ID, base64Url(publicKey.getModulus()), base64Url(publicKey.getPublicExponent()));
    }

    private static String base64Url(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
