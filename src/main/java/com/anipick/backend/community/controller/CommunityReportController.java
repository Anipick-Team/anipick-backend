package com.anipick.backend.community.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.ReportCreateRequest;
import com.anipick.backend.community.service.CommunityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/reports")
@RequiredArgsConstructor
public class CommunityReportController {

    private final CommunityReportService communityReportService;

    @PostMapping
    public ApiResponse<Void> createReport(
            @RequestBody ReportCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        communityReportService.createReport(request, userId);
        return ApiResponse.success();
    }
}
