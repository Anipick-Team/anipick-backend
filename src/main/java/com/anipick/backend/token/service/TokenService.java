package com.anipick.backend.token.service;

import com.anipick.backend.common.auth.JwtTokenProvider;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.domain.RefreshToken;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.user.domain.UserDefaults;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, RefreshToken> redisTemplate;
    Duration duration = Duration.ofDays(UserDefaults.DEFAULT_REFRESH_TOKEN_EXPIRATION_DAYS);

    public TokenResponse generateAndSaveTokens(String email) {
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        RefreshToken savedRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .build();
        redisTemplate.opsForValue().set(email, savedRefreshToken, duration);

        return TokenResponse.fromPairToken(accessToken, refreshToken);
    }

    public ApiResponse<TokenResponse> reissueToken(HttpServletRequest request) {
        String requestedToken = jwtTokenProvider.resolveAccessToken(request);
        String email = jwtTokenProvider.getEmailFromToken(requestedToken);

        RefreshToken refreshToken = redisTemplate.opsForValue().get(email);

        if (refreshToken != null && !refreshToken.getToken().equals(requestedToken)) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email);

        return ApiResponse.success(TokenResponse.fromPairToken(newAccessToken, requestedToken));
    }
}
