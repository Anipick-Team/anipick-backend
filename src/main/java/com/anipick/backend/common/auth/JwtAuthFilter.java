package com.anipick.backend.common.auth;

import com.anipick.backend.common.auth.service.CustomUserDetailsService;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.UserDefaults;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
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
            if(accessToken != null) {
                jwtTokenProvider.validateToken(accessToken); //토큰 유효성 검사

                String checkLogout = redisTemplate.opsForValue().get(UserDefaults.DEFAULT_LOGOUT_LIST_FORMAT + accessToken);
                if(checkLogout != null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");

                    ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.REQUESTED_TOKEN_INVALID);
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(apiResponse);
                    response.getWriter().write(json);
                    
                    return;
                }
                
                String emailFromToken = jwtTokenProvider.getEmailFromToken(accessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(emailFromToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");

            ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.REQUESTED_TOKEN_INVALID);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(apiResponse);
            response.getWriter().write(json);
        }
    }
}
