package com.anipick.backend.common.auth;

import com.anipick.backend.common.auth.service.CustomUserDetailsService;
import com.anipick.backend.user.domain.UserDefaults;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        try {
            if (accessToken != null) {
                jwtTokenProvider.validateToken(accessToken); // 토큰 유효성 검사

                String checkLogout = redisTemplate.opsForValue().get(UserDefaults.DEFAULT_LOGOUT_LIST_FORMAT_KEY + accessToken);
                if (checkLogout == null) {
                    String emailFromToken = jwtTokenProvider.getEmailFromToken(accessToken);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(emailFromToken);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                // 로그아웃(블랙리스트)된 토큰이면 인증을 세팅하지 않는다 → 보호 엔드포인트는 EntryPoint가 401 처리
            }
        } catch (Exception e) {
            // 만료·무효·orphan 토큰: 인증만 생략하고 요청은 계속 진행한다.
            // 공개 엔드포인트(login/oauth 등)는 그대로 통과, 보호 엔드포인트는 Spring Security가 401 처리한다.
            log.debug("JWT 인증 스킵: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
