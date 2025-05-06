package com.anipick.backend.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final int accessTokenExpiration;
    private final int refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expire-time}") int accessTokenExpiration,
            @Value("${jwt.refresh-token-expire-time}") int refreshTokenExpiration
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(String email) {
        return createToken(email, accessTokenExpiration);
    }

    public String createRefreshToken(String email) {
        return createToken(email, refreshTokenExpiration);
    }

    private String createToken(String email, int expireTime) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(jwtSecretKey())
                .compact();
    }

    private SecretKey jwtSecretKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }
}
