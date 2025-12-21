package com.anipick.backend.admin.controller;

import com.anipick.backend.admin.dto.*;
import com.anipick.backend.admin.service.AdminService;
import com.anipick.backend.common.auth.dto.CustomAdminDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.dto.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private static final String ADMIN_REFRESH_TOKEN = "ADMIN_REFRESH_TOKEN";
    private static final String SAME_SITE = "Strict";
    private static final int REFRESH_TOKEN_MAX_AGE_SECOND = 1209600;

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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/version")
    public ApiResponse<Void> createVersion(
            @Valid @RequestBody CreateVersionRequestDto request,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        adminService.createVersion(request);
        return ApiResponse.success();
    }

    // 버전 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/version")
    public ApiResponse<VersionListResultDto> getVersions(
            @RequestParam(value = "platform") String platform,
            @RequestParam(value = "type", required = false) String type,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        VersionListResultDto items = adminService.getVersionItems(platform, type);
        return ApiResponse.success(items);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/version/{versionId}")
    public ApiResponse<VersionResultDto> getVersion(
            @PathVariable(value = "versionId") Long versionId,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        VersionResultDto item = adminService.getVersionItem(versionId);
        return ApiResponse.success(item);
    }

    // 버전 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/version/{versionId}")
    public ApiResponse<Void> updateVersion(
            @PathVariable(value = "versionId") Long versionId,
            @Valid @RequestBody CreateVersionRequestDto request,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        adminService.updateVersion(versionId, request);
        return ApiResponse.success();
    }

    // 버전 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/version/{versionId}")
    public ApiResponse<Void> deleteVersion(
            @PathVariable(value = "versionId") Long versionId,
            @AuthenticationPrincipal CustomAdminDetails admin
    ) {
        adminService.deleteVersion(versionId);
        return ApiResponse.success();
    }

    private static void checkUsernamePassword(AdminUsernamePasswordRequestDto request) {
        boolean validUsernameAndPassword = AdminUsernamePasswordRequestDto
                .checkValidUsernameAndPassword(request.getUsername(), request.getPassword());
        if (!validUsernameAndPassword) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    private static AccessTokenResponse issueTokensToResponse(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie cookie = ResponseCookie
                .from(ADMIN_REFRESH_TOKEN, tokenResponse.getRefreshToken())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(REFRESH_TOKEN_MAX_AGE_SECOND)
                .sameSite(SAME_SITE)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        AccessTokenResponse accessTokenResponse = AccessTokenResponse
                .of(tokenResponse.getAccessToken());
        return accessTokenResponse;
    }
}
