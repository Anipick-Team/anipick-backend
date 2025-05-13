package com.anipick.backend.token.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(HttpServletRequest request) {
        TokenResponse response = tokenService.reissueToken(request);
        return ApiResponse.success(response);
    }
}
