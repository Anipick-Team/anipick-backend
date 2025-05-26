package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.domain.KakaoDefaults;
import com.anipick.backend.oauth.domain.Platform;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.oauth.util.GoogleVerifierUtil;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.service.NicknameInitializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final GoogleVerifierUtil googleVerifierUtil;
    private final WebClient webClient;

    public TokenResponse socialLogin(SocialLoginRequest request, Provider provider) {
        String platform = request.getPlatform();
        String code = request.getCode();
        Platform platformEnum = Platform.valueOf(platform.toUpperCase());

        switch (provider) {
            case GOOGLE:
                return googleLogin(platformEnum, code);

            case KAKAO:
                return kakaoLogin(platformEnum, code);
        }

        return null;
    }

    private TokenResponse googleLogin(Platform platform, String idToken) {
        try {
            GoogleIdToken.Payload payload;
            if(platform.equals(Platform.ANDROID)) {
                payload = googleVerifierUtil.verifyAndroidGoogleToken(idToken);
            } else if(platform.equals(Platform.IOS)) {
                payload = googleVerifierUtil.verifyIosGoogleToken(idToken);
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            String email = payload.getEmail();
            return signUpAndLogin(email, LoginFormat.GOOGLE);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private TokenResponse kakaoLogin(Platform platform, String accessToken) {
        try {
            String response = webClient.post()
                    .uri(KakaoDefaults.DEFAULT_POST_URI)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode account = root.path(KakaoDefaults.DEFAULT_ACCOUNT_ROOT_PATH);
            String email = account.path(KakaoDefaults.DEFAULT_EMAIL_PATH).asText(null);

            if(email == null || email.isEmpty()) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL);
            }

            return signUpAndLogin(email, LoginFormat.KAKAO);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private TokenResponse signUpAndLogin(String email, LoginFormat loginFormat) {
        if(checkExistsEmail(email)) {
            return tokenService.generateAndSaveTokens(email);
        } else {
            String nickname = NicknameInitializer.generateUniqueNickname(userMapper::existsByNickname);
            User user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .loginFormat(loginFormat)
                    .profileImageUrl(UserDefaults.DEFAULT_PROFILE_IMAGE_URL)
                    .termsAndConditions(true)
                    .adultYn(UserDefaults.DEFAULT_ADULT_YN)
                    .reviewCompletedYn(UserDefaults.DEFAULT_REVIEW_COMPLETED_YN)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userMapper.insertUser(user);

            return tokenService.generateAndSaveTokens(email);
        }
    }

    private boolean checkExistsEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }
}
