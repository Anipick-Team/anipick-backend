package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.component.CommonLogin;
import com.anipick.backend.oauth.domain.KakaoDefaults;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.user.domain.LoginFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KakaoLogin implements SocialLogin {
    private final CommonLogin commonLogin;
    private final RestClient restClient = RestClient.builder()
            .baseUrl(KakaoDefaults.DEFAULT_REST_CLIENT_BASE_URL)
            .build();
    private final ObjectMapper objectMapper;

    @Override
    public boolean checkProvider(Provider provider) {
        return provider == Provider.KAKAO;
    }

    @Override
    public TokenResponse login(SocialLoginRequest request) {
        String accessToken = request.getCode();

        try {
            String response = restClient.post()
                    .uri(KakaoDefaults.DEFAULT_POST_URI)
                    .headers(header -> header.setBearerAuth(accessToken))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode account = root.path(KakaoDefaults.DEFAULT_ACCOUNT_ROOT_PATH);
            String email = account.path(KakaoDefaults.DEFAULT_EMAIL_PATH).asText(null);

            if(email == null || email.isEmpty()) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL);
            }

            return commonLogin.signUpAndLogin(email, LoginFormat.KAKAO);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
