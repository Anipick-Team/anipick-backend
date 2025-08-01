package com.anipick.backend.mypage.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.mypage.dto.*;
import com.anipick.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/profile-image")
    public ApiResponse<ProfileImageResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("profileImageRequest") ProfileImageRequest request,
            @RequestPart("profileImageFile") MultipartFile profileImageFile
    ) {
        ProfileImageResponse response = myPageService.updateProfileImage(user, request, profileImageFile);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/watchlist")
    public ApiResponse<WatchListAnimesResponse> getMyAnimesWatchList(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size
    ) {
        Long userId = user.getUserId();
        WatchListAnimesResponse response = myPageService.getMyAnimesWatchList(userId, status, lastId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/watching")
    public ApiResponse<WatchingAnimesResponse> getMyAnimesWatching(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size
    ) {
        Long userId = user.getUserId();
        WatchingAnimesResponse response = myPageService.getMyAnimesWatching(userId, status, lastId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/finished")
    public ApiResponse<FinishedAnimesResponse> getMyAnimesFinished(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size
    ) {
        Long userId = user.getUserId();
        FinishedAnimesResponse response = myPageService.getMyAnimesFinished(userId, status, lastId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/rated")
    public ApiResponse<RatedAnimesResponse> getMyAnimesRated(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount,
            @RequestParam(value = "lastRating", required = false) Double lastRating,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
            @RequestParam(value = "reviewOnly", required = false, defaultValue = "false") Boolean reviewOnly
    ) {
        Long userId = user.getUserId();
        RatedAnimesResponse response = myPageService.getMyAnimesRated(userId, lastId, lastLikeCount, lastRating, size, sort, reviewOnly);
        return ApiResponse.success(response);
    }

    @GetMapping("/animes/like")
    public ApiResponse<LikedAnimesResponse> getMyAnimesLiked(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size
    ) {
        Long userId = user.getUserId();
        LikedAnimesResponse response = myPageService.getMyAnimesLiked(userId, lastId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/persons/like")
    public ApiResponse<LikedPersonsResponse> getMyPersonsLiked(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", required = false, defaultValue = "18") Integer size
    ) {
        Long userId = user.getUserId();
        LikedPersonsResponse response = myPageService.getMyPersonsLiked(userId, lastId, size);
        return ApiResponse.success(response);
    }
}
