package com.anipick.backend.oauth.component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.webtoken.JsonWebSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleVerifierProcessorTest {
    private static final String ANDROID_OLD = "android-old.apps.googleusercontent.com";
    private static final String ANDROID_NEW = "android-new.apps.googleusercontent.com";
    private static final String IOS_A = "ios-a.apps.googleusercontent.com";
    private static final String IOS_B = "ios-b.apps.googleusercontent.com";

    @Test
    @DisplayName("Android client ID가 하나면 그 값 하나만 허용한다")
    void androidClientIds_single() {
        GoogleVerifierProcessor sut = new GoogleVerifierProcessor(ANDROID_OLD, IOS_A);

        assertThat(androidClientIds(sut)).containsExactly(ANDROID_OLD);
    }

    @Test
    @DisplayName("Android client ID를 쉼표로 이으면 공백을 지우고 빈 값은 버린 뒤 각각 허용한다")
    void androidClientIds_multiple() {
        GoogleVerifierProcessor sut = new GoogleVerifierProcessor(" " + ANDROID_OLD + " , " + ANDROID_NEW + " ,, ", IOS_A);

        assertThat(androidClientIds(sut)).containsExactly(ANDROID_OLD, ANDROID_NEW);
    }

    @Test
    @DisplayName("iOS client ID도 같은 규칙으로 쉼표를 나눈다")
    void iosClientIds_multiple() {
        GoogleVerifierProcessor sut = new GoogleVerifierProcessor(ANDROID_OLD, IOS_A + ", " + IOS_B);

        assertThat(iosClientIds(sut)).containsExactly(IOS_A, IOS_B);
    }

    @Test
    @DisplayName("Android client ID를 여러 개 두면 각 ID로 발급된 토큰이 audience 검증을 통과하고 목록에 없는 ID는 거부한다")
    void androidAudience_acceptsEveryConfiguredId() {
        GoogleVerifierProcessor sut = new GoogleVerifierProcessor(ANDROID_OLD + "," + ANDROID_NEW, IOS_A);
        List<String> audience = androidClientIds(sut);

        assertThat(tokenFor(ANDROID_OLD).verifyAudience(audience)).isTrue();
        assertThat(tokenFor(ANDROID_NEW).verifyAudience(audience)).isTrue();
        assertThat(tokenFor(IOS_A).verifyAudience(audience)).isFalse();
    }

    private static GoogleIdToken tokenFor(String aud) {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload().setAudience(aud);
        return new GoogleIdToken(new JsonWebSignature.Header(), payload, new byte[0], new byte[0]);
    }

    @SuppressWarnings("unchecked")
    private static List<String> androidClientIds(GoogleVerifierProcessor sut) {
        return (List<String>) ReflectionTestUtils.getField(sut, "androidClientIds");
    }

    @SuppressWarnings("unchecked")
    private static List<String> iosClientIds(GoogleVerifierProcessor sut) {
        return (List<String>) ReflectionTestUtils.getField(sut, "iosClientIds");
    }
}
