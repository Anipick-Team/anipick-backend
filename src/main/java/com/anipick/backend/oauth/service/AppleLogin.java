package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.component.AppleVerifierProcessor;
import com.anipick.backend.oauth.component.CommonLogin;
import com.anipick.backend.oauth.domain.AppleDefaults;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.LoginResponse;
import com.anipick.backend.user.domain.LoginFormat;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleLogin implements SocialLogin {
    private final AppleVerifierProcessor appleVerifierProcessor;
    private final CommonLogin commonLogin;

    @Override
    public boolean checkProvider(Provider provider) {
        return provider == Provider.APPLE;
    }

    @Override
    public LoginResponse login(SocialLoginRequest request) {
        String code = request.getCode();

        try {
            String email = isIdentityToken(code) ? extractEmail(code) : code;
            return commonLogin.signUpAndLogin(email, LoginFormat.APPLE);
        } catch (CustomException e) {
            throw e; // CustomException은 그대로 전파
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: iOS identityToken 버전 배포 후 구버전(code = 이메일) 지원 제거
    private boolean isIdentityToken(String code) {
        return code != null && code.chars().filter(c -> c == '.').count() == 2 && !code.contains("@");
    }

    private String extractEmail(String identityToken) {
        Claims claims = appleVerifierProcessor.verifyIdentityToken(identityToken);
        String email = claims.get(AppleDefaults.DEFAULT_EMAIL_CLAIM, String.class);

        if (email == null || email.isEmpty()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL);
        }
        return email;
    }
}
