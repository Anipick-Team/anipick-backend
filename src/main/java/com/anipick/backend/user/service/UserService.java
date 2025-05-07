package com.anipick.backend.user.service;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.dto.SignUpRequest;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<User> signUp(SignUpRequest request) {
        String requestEmail = request.getEmail();
        String requestPassword = request.getPassword();

        // 이메일 중복 확인
        if(checkDuplicateEmail(requestEmail)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        // 이메일 형식 확인
        if(!SignUpValidator.checkValidationEmail(requestEmail)) {
            throw new CustomException(ErrorCode.EMAIL_INVALID_FORMAT);
        }

        // 비밀번호 형식 확인
        if(!SignUpValidator.checkValidationPassword(requestPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_INVALID_FORMAT);
        }

        String nickname = NicknameInitializer.generateUniqueNickname(userMapper::existsByNickname);
        User user = User.builder()
                .email(requestEmail)
                .password(passwordEncoder.encode(requestPassword))
                .nickname(nickname)
                .loginFormat(LoginFormat.LOCAL)
                .profileImageUrl(UserDefaults.DEFAULT_PROFILE_IMAGE_URL)
                .termsAndConditions(request.getTermsAndConditions())
                .adultYn(UserDefaults.DEFAULT_ADULT_YN)
                .reviewCompletedYn(UserDefaults.DEFAULT_REVIEW_COMPLETED_YN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insertUser(user);

        return ApiResponse.success(user);
    }

    private boolean checkDuplicateEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }
}
