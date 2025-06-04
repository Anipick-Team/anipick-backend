package com.anipick.backend.oauth.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.oauth.domain.Provider;
import com.anipick.backend.oauth.dto.SocialLoginRequest;
import com.anipick.backend.oauth.service.OAuthService;
import com.anipick.backend.token.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/{provider}/callback")
    public ApiResponse<LoginResponse> socialLogin(@RequestBody SocialLoginRequest request, @PathVariable String provider) {
        Provider oAuthProvider = Provider.valueOf(provider.toUpperCase());
        LoginResponse response = oAuthService.socialLogin(request, oAuthProvider);
        return ApiResponse.success(response);
    }
}
