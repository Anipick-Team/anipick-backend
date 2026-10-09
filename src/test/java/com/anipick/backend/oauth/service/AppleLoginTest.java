package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.component.AppleVerifierProcessor;
import com.anipick.backend.oauth.component.CommonLogin;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.user.domain.LoginFormat;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AppleLoginTest {
    private static final String IDENTITY_TOKEN = "header.payload.signature";

    @Mock
    AppleVerifierProcessor appleVerifierProcessor;

    @Mock
    CommonLogin commonLogin;

    @InjectMocks
    AppleLogin sut;

    @Test
    @DisplayName("identityToken이면 검증 후 토큰의 이메일로 로그인한다")
    void login_withIdentityToken() {
        Claims claims = Jwts.claims().add("email", "user@privaterelay.appleid.com").build();
        given(appleVerifierProcessor.verifyIdentityToken(IDENTITY_TOKEN)).willReturn(claims);

        sut.login(request(IDENTITY_TOKEN));

        verify(commonLogin).signUpAndLogin("user@privaterelay.appleid.com", LoginFormat.APPLE);
    }

    @Test
    @DisplayName("identityToken에 이메일이 없으면 가입시키지 않고 ACCOUNT_NOT_FOUND_BY_EMAIL")
    void login_withIdentityTokenWithoutEmail() {
        given(appleVerifierProcessor.verifyIdentityToken(IDENTITY_TOKEN)).willReturn(Jwts.claims().build());

        assertThatThrownBy(() -> sut.login(request(IDENTITY_TOKEN)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL);
        verify(commonLogin, never()).signUpAndLogin(any(), any());
    }

    @Test
    @DisplayName("검증 실패 시 REQUESTED_TOKEN_INVALID를 그대로 전파한다")
    void login_withInvalidIdentityToken() {
        given(appleVerifierProcessor.verifyIdentityToken(IDENTITY_TOKEN))
                .willThrow(new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID));

        assertThatThrownBy(() -> sut.login(request(IDENTITY_TOKEN)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUESTED_TOKEN_INVALID);
        verify(commonLogin, never()).signUpAndLogin(any(), any());
    }

    @Test
    @DisplayName("구버전 앱(code = 이메일)은 기존처럼 이메일로 로그인한다")
    void login_withLegacyEmail() {
        sut.login(request("first.last@example.com"));

        verifyNoInteractions(appleVerifierProcessor);
        verify(commonLogin).signUpAndLogin("first.last@example.com", LoginFormat.APPLE);
    }

    private static SocialLoginRequest request(String code) {
        SocialLoginRequest request = new SocialLoginRequest();
        ReflectionTestUtils.setField(request, "platform", "IOS");
        ReflectionTestUtils.setField(request, "code", code);
        return request;
    }
}
