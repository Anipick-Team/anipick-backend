package com.anipick.backend.admin.controller;

import com.anipick.backend.admin.dto.AdminSignupRequest;
import com.anipick.backend.admin.service.AdminService;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<Void> signup(@RequestBody AdminSignupRequest request) {
        boolean validUsernameAndPassword = AdminSignupRequest
                .checkValidUsernameAndPassword(request.getUsername(), request.getPassword());
        if (!validUsernameAndPassword) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        adminService.signup(request);
        return ApiResponse.success();
    }
    // 관리자 로그인
    // 버전 등록
    // 버전 조회
    // 버전 수정
    // 버전 삭제
}
