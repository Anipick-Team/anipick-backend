package com.anipick.backend.admin.controller;

import com.anipick.backend.admin.dto.AccessTokenResponse;
import com.anipick.backend.admin.dto.AdminUsernamePasswordRequestDto;
import com.anipick.backend.admin.dto.CreateVersionRequestDto;
import com.anipick.backend.admin.service.AdminService;
import com.anipick.backend.common.auth.dto.CustomAdminDetails;
import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 관리자 계정 생성
    @PostMapping("/signup")
    public ApiResponse<Void> signup(@RequestBody AdminUsernamePasswordRequestDto request) {
        checkUsernamePassword(request);
        adminService.signup(request);
        return ApiResponse.success();
    }

    // 관리자 로그인
    @PostMapping("/login")
    public ApiResponse<AccessTokenResponse> login(
            @RequestBody AdminUsernamePasswordRequestDto request,
            HttpServletResponse response
            ) {
        checkUsernamePassword(request);
        TokenResponse tokenResponse = adminService.login(request);

        AccessTokenResponse accessTokenResponse = issueTokensToResponse(response, tokenResponse);

        return ApiResponse.success(accessTokenResponse);
    }

    // 버전 등록

    @PostMapping("/version")
    public ApiResponse<Void> createVersion(
//            @RequestBody CreateVersionRequestDto request,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        Long adminId = admin.getAdminId();
        System.out.println("adminId = " + adminId);
        return null;
    }
    // 버전 조회
    // 버전 수정
    // 버전 삭제
    private static void checkUsernamePassword(AdminUsernamePasswordRequestDto request) {
        boolean validUsernameAndPassword = AdminUsernamePasswordRequestDto
                .checkValidUsernameAndPassword(request.getUsername(), request.getPassword());
        if (!validUsernameAndPassword) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    private static AccessTokenResponse issueTokensToResponse(HttpServletResponse response, TokenResponse tokenResponse) {
        Cookie refreshCookie = new Cookie(
                "ADMIN_REFRESH_TOKEN",
                tokenResponse.getRefreshToken()
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((1209600000 / 1000));

        response.addCookie(refreshCookie);

        AccessTokenResponse accessTokenResponse = AccessTokenResponse
                .of(tokenResponse.getAccessToken());
        return accessTokenResponse;
    }
}
