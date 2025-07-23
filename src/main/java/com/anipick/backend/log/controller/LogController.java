package com.anipick.backend.log.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping("/{logPath}")
    public ApiResponse<Void> createLog(
            @PathVariable(value = "logPath") String logPath,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        logService.createLog(logPath);
        return ApiResponse.success();
    }
}
