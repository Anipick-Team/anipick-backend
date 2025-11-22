package com.anipick.backend.version.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.oauth.domain.Platform;
import com.anipick.backend.version.dto.UserAppVersionRequestDto;
import com.anipick.backend.version.dto.VersionResultDto;
import com.anipick.backend.version.service.UserAppVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserAppVersionController {
    private final UserAppVersionService userAppVersionService;

    @GetMapping("/version")
    public ApiResponse<VersionResultDto> getUserAppVersion(
            @RequestParam(value = "userAppVersion") String userAppVersion,
            @RequestParam(value = "platform") Platform platform
    ) {
        UserAppVersionRequestDto request = UserAppVersionRequestDto.from(userAppVersion, platform);
        VersionResultDto result = userAppVersionService.getUserAppVersion(request);
        return ApiResponse.success(result);
    }
}
