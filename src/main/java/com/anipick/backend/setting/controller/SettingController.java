package com.anipick.backend.setting.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.setting.dto.ChangeEmailRequest;
import com.anipick.backend.setting.dto.ChangeNicknameRequest;
import com.anipick.backend.setting.dto.ChangePasswordRequest;
import com.anipick.backend.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting")
public class SettingController {
    private final SettingService settingService;

    @PatchMapping("/nickname")
    public ApiResponse<Void> changeNickname(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ChangeNicknameRequest request) {
        Long userId = user.getUserId();
        settingService.changeNickname(userId, request);
        return ApiResponse.success();
    }

    @PutMapping("/email")
    public ApiResponse<Void> changeEmail(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ChangeEmailRequest request) {
        settingService.changeEmail(user, request);
        return ApiResponse.success();
    }

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ChangePasswordRequest request) {
        settingService.changePassword(user, request);
        return ApiResponse.success();
    }

    @PatchMapping("/withdrawal")
    public ApiResponse<Void> userWithdrawal(@AuthenticationPrincipal CustomUserDetails user) {
        settingService.userWithdrawal(user);
        return ApiResponse.success();
    }
}
