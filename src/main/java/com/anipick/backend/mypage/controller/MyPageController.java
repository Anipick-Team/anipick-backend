package com.anipick.backend.mypage.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.mypage.dto.MyPageResponse;
import com.anipick.backend.mypage.dto.WatchListAnimesResponse;
import com.anipick.backend.mypage.dto.WatchingAnimesResponse;
import com.anipick.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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

    @GetMapping("/animes/watchlist")
    public ApiResponse<WatchListAnimesResponse> getMyAnimesWatchList(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size,
            @RequestParam(value = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
    ) {
        Long userId = user.getUserId();
        WatchListAnimesResponse response = myPageService.getMyAnimesWatchList(userId, status, lastId, size, lastCreatedAt);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/watching")
    public ApiResponse<WatchingAnimesResponse> getMyAnimesWatching(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size,
            @RequestParam(value = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
    ) {
        Long userId = user.getUserId();
        WatchingAnimesResponse response = myPageService.getMyAnimesWatching(userId, status, lastId, size, lastCreatedAt);
        return ApiResponse.success(response);
    }
}
