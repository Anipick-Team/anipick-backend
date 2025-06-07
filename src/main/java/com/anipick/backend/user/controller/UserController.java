package com.anipick.backend.user.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.token.dto.LoginResponse;
import com.anipick.backend.token.dto.SignUpResponse;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.user.dto.LoginRequest;
import com.anipick.backend.user.dto.SignUpRequest;
import com.anipick.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

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
}
