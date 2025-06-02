package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.component.CommonLogin;
import com.anipick.backend.oauth.domain.AppleDefaults;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.user.domain.LoginFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleLogin implements SocialLogin {
    private final CommonLogin commonLogin;

    @Override
    public boolean checkProvider(Provider provider) {
        return provider == Provider.APPLE;
    }

    @Override
    public TokenResponse login(SocialLoginRequest request) {
        String prefixEmail = request.getCode();

        try {
            String email = AppleDefaults.DEFAULT_APPLE_EMAIL_FORMAT
                    .formatted(prefixEmail);
            return commonLogin.signUpAndLogin(email, LoginFormat.APPLE);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
