package com.anipick.backend.mypage.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.mypage.dto.MyPageResponse;
import com.anipick.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping
    public ApiResponse<MyPageResponse> getMyPage(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        MyPageResponse response = myPageService.getMyPage(userId);
        return ApiResponse.success(response);
    }
}
