package com.anipick.backend.oauth.service;

import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.TokenResponse;

public interface SocialLogin {
    boolean checkProvider(Provider provider);
    TokenResponse login(SocialLoginRequest request);
}
