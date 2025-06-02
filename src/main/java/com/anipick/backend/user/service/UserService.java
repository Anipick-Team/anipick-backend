package com.anipick.backend.user.service;

import com.anipick.backend.common.auth.JwtTokenProvider;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import com.anipick.backend.user.component.NicknameInitializer;
import com.anipick.backend.user.dto.LoginRequest;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserDefaults;
import com.anipick.backend.user.dto.SignUpRequest;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.util.SignUpValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final NicknameInitializer nicknameInitializer;

    public void signUp(SignUpRequest request) {
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

        User user = User.builder()
                .email(requestEmail)
                .password(passwordEncoder.encode(requestPassword))
                .nickname(nicknameInitializer.generateNickname(LoginFormat.LOCAL))
                .loginFormat(LoginFormat.LOCAL)
                .profileImageUrl(UserDefaults.DEFAULT_PROFILE_IMAGE_URL)
                .termsAndConditions(request.getTermsAndConditions())
                .adultYn(UserDefaults.DEFAULT_ADULT_YN)
                .reviewCompletedYn(UserDefaults.DEFAULT_REVIEW_COMPLETED_YN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insertUser(user);
    }

    private boolean checkDuplicateEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }

    public TokenResponse doLogin(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        return tokenService.generateAndSaveTokens(user.getEmail());
    }

    public void doLogout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if(accessToken == null) {
            return;
        }

        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        jwtTokenProvider.validateToken(accessToken);
        long expiration = jwtTokenProvider.getRemainingTokenExpiration(accessToken);
        Duration duration = Duration.ofMillis(expiration);

        redisTemplate.opsForValue().set(UserDefaults.DEFAULT_LOGOUT_LIST_FORMAT_KEY + accessToken, "logout", duration);
        redisTemplate.delete(email);
    }
}
