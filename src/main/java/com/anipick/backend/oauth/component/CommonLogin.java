package com.anipick.backend.oauth.component;

import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.service.NicknameInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommonLogin {
    private final UserMapper userMapper;
    private final TokenService tokenService;

    public TokenResponse signUpAndLogin(String email, LoginFormat loginFormat) {
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
