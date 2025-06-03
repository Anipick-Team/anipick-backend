package com.anipick.backend.auth.controller;

import com.anipick.backend.auth.dto.AuthCodeRequest;
import com.anipick.backend.auth.dto.AuthEmailRequest;
import com.anipick.backend.auth.dto.AuthPasswordRequest;
import com.anipick.backend.auth.service.AuthService;
import com.anipick.backend.common.dto.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/email/send")
    public ApiResponse<Void> sendEmail(@RequestBody AuthEmailRequest request) throws MessagingException, NoSuchAlgorithmException {
        authService.send(request);
        return ApiResponse.success();
    }

    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmail(@RequestBody AuthCodeRequest request) {
        authService.verify(request);
        return ApiResponse.success();
    }

    @PatchMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@RequestBody AuthPasswordRequest request) {
        authService.reset(request);
        return ApiResponse.success();
    }
}
