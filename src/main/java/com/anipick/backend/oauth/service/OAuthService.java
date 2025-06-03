package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.TokenResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final List<SocialLogin> socialLogins;

    public TokenResponse socialLogin(SocialLoginRequest request, Provider provider) {
        return socialLogins.stream()
                .filter(login -> login.checkProvider(provider))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST))
                .login(request);
    }
}
