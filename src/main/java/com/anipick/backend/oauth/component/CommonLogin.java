package com.anipick.backend.oauth.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.dto.LoginResponse;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import com.anipick.backend.user.component.NicknameInitializer;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommonLogin {
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final NicknameInitializer nicknameInitializer;

    public LoginResponse signUpAndLogin(String email, LoginFormat loginFormat) {
        if(checkExistsEmail(email)) {
            User user = userMapper.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));
            TokenResponse response = tokenService.generateAndSaveTokens(email);

            return LoginResponse.from(user.getReviewCompletedYn(), user.getUserId(), user.getNickname(), response);
        } else {
            String nickname = nicknameInitializer.generateNickname(loginFormat);
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

            TokenResponse response = tokenService.generateAndSaveTokens(email);
            return LoginResponse.from(user.getReviewCompletedYn(), user.getUserId(), user.getNickname(), response);
        }
    }

    private boolean checkExistsEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }
}
