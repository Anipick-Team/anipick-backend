package com.anipick.backend.token.service;

import com.anipick.backend.common.auth.JwtTokenProvider;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.domain.RefreshToken;
import com.anipick.backend.token.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, RefreshToken> redisTemplate;
    private final long refreshTokenExpireTime;

    public TokenService(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, RefreshToken> redisTemplate, @Value("${jwt.refresh-token-expire-time}") long refreshTokenExpireTime) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public TokenResponse generateAndSaveTokens(String account, String role) {
        String accessToken = jwtTokenProvider.createAccessToken(account, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(account, role);

        RefreshToken savedRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .role(role)
                .build();

        Duration duration = Duration.ofMillis(refreshTokenExpireTime);
        redisTemplate.opsForValue().set(account, savedRefreshToken, duration);

        return TokenResponse.fromPairToken(accessToken, refreshToken);
    }

    public TokenResponse reissueToken(HttpServletRequest request) {
        String requestedToken = jwtTokenProvider.resolveAccessToken(request);
        String subject = jwtTokenProvider.getClaims(requestedToken).getSubject();

        RefreshToken refreshToken = redisTemplate.opsForValue().get(subject);

        if (refreshToken != null && !refreshToken.getToken().equals(requestedToken)) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(subject, refreshToken.getRole());

        return TokenResponse.fromPairToken(newAccessToken, requestedToken);
    }
}
