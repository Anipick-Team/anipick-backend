package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.component.CommonLogin;
import com.anipick.backend.oauth.component.GoogleVerifierProcessor;
import com.anipick.backend.oauth.domain.Platform;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.LoginResponse;
import com.anipick.backend.user.domain.LoginFormat;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLogin implements SocialLogin {
    private final GoogleVerifierProcessor googleVerifierProcessor;
    private final CommonLogin commonLogin;

    @Override
    public boolean checkProvider(Provider provider) {
        return provider == Provider.GOOGLE;
    }

    @Override
    public LoginResponse login(SocialLoginRequest request) {
        Platform platform = Platform.valueOf(request.getPlatform().toUpperCase());
        String idToken = request.getCode();
        String email = null;

        try {
            GoogleIdToken.Payload payload;
            if(platform.equals(Platform.ANDROID)) {
                payload = googleVerifierProcessor.verifyAndroidGoogleToken(idToken);
            } else if(platform.equals(Platform.IOS)) {
                payload = googleVerifierProcessor.verifyIosGoogleToken(idToken);
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            email = payload.getEmail();
            return commonLogin.signUpAndLogin(email, LoginFormat.GOOGLE);
        } catch (CustomException e) {
            log.warn("[소셜 로그인 실패] provider={}, email={}, reason={}",
                    Provider.GOOGLE, email, e.getErrorCode().name());
            throw e; // CustomException은 그대로 전파
        } catch (Exception e) {
            log.error("[소셜 로그인 실패] provider={}, email={}, reason={}",
                    Provider.GOOGLE, email, ErrorCode.INTERNAL_SERVER_ERROR.name(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
