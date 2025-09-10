package com.anipick.backend.user.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.token.dto.LoginResponse;
import com.anipick.backend.token.dto.SignUpResponse;
import com.anipick.backend.user.dto.LoginRequest;
import com.anipick.backend.user.dto.SignUpRequest;
import com.anipick.backend.user.dto.UserAnimeStatusRequest;
import com.anipick.backend.user.service.UserAnimeStatusService;
import com.anipick.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserAnimeStatusService userAnimeStatusService;

    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        SignUpResponse response = userService.signUp(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.doLogin(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        userService.doLogout(request);
        return ApiResponse.success();
    }

    @PostMapping("/{animeId}/status")
    public ApiResponse<Void> createUserAnimeOfStatus (
            @PathVariable(value = "animeId") Long animeId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody UserAnimeStatusRequest status
            ) {
        Long userId = user.getUserId();
        userAnimeStatusService.createUserAnimeOfStatus(animeId, userId, status);
        return ApiResponse.success();
    }

    @PatchMapping("/{animeId}/status")
    public ApiResponse<Void> updateUserAnimeOfStatus (
            @PathVariable(value = "animeId") Long animeId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody UserAnimeStatusRequest status
            ) {
        Long userId = user.getUserId();
        userAnimeStatusService.updateUserAnimeOfStatus(animeId, userId, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/{animeId}/status")
    public ApiResponse<Void> deleteUserAnimeOfStatus (
            @PathVariable(value = "animeId") Long animeId,
            @AuthenticationPrincipal CustomUserDetails user
            ) {
        Long userId = user.getUserId();
        userAnimeStatusService.deleteUserAnimeOfStatus(animeId, userId);
        return ApiResponse.success();
    }
}
