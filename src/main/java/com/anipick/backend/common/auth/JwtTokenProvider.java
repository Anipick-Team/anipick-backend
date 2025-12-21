package com.anipick.backend.common.auth;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;

    @Getter
    private final long accessTokenExpiration;
    @Getter
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expire-time}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expire-time}") long refreshTokenExpiration
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(String account, String role) {
        return createToken(account, role, accessTokenExpiration);
    }

    public String createRefreshToken(String account, String role) {
        return createToken(account, role, refreshTokenExpiration);
    }

    private String createToken(String account, String role, long expireTime) {
        Date now = new Date();

        return Jwts.builder()
                .subject(account)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireTime))
                .signWith(jwtSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecretKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public long getRemainingTokenExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();
            long diff = expiration.getTime() - now;

            return Math.max(diff, 0);
        } catch (JwtException | IllegalArgumentException e) {
            return 0L;
        }
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(jwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.REQUESTED_TOKEN_INVALID);
        }
    }

    private SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
