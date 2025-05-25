package com.anipick.backend.oauth.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.oauth.util.GoogleVerifierUtil;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.service.NicknameInitializer;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final GoogleVerifierUtil googleVerifierUtil;

    public TokenResponse socialLogin(SocialLoginRequest request, String provider) {
        String platform = request.getPlatform();
        String idToken = request.getCode();

        switch (provider) {
            case "GOOGLE":
                return googleLogin(platform, idToken);
        }

        return null;
    }

    private TokenResponse googleLogin(String platform, String idToken) {
        try {
            GoogleIdToken.Payload payload;
            if(platform.equals("android")) {
                payload = googleVerifierUtil.verifyAndroidGoogleToken(idToken);
            } else if(platform.equals("ios")) {
                payload = googleVerifierUtil.verifyIosGoogleToken(idToken);
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            String email = payload.getEmail();
            if(checkExistsEmail(email)) {
                return tokenService.generateAndSaveTokens(email);
            } else {
                String nickname = NicknameInitializer.generateUniqueNickname(userMapper::existsByNickname);
                User user = User.builder()
                        .email(email)
                        .nickname(nickname)
                        .loginFormat(LoginFormat.GOOGLE)
                        .profileImageUrl(UserDefaults.DEFAULT_PROFILE_IMAGE_URL)
                        .termsAndConditions(true)
                        .adultYn(UserDefaults.DEFAULT_ADULT_YN)
                        .reviewCompletedYn(UserDefaults.DEFAULT_REVIEW_COMPLETED_YN)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                userMapper.insertUser(user);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return null;
    }

    private boolean checkExistsEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }
}
