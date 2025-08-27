package com.anipick.backend.mypage.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.mypage.dto.*;
import com.anipick.backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Slf4j
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping
    public ApiResponse<MyPageResponse> getMyPage(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        MyPageResponse response = myPageService.getMyPage(userId);
        return ApiResponse.success(response);
    }

    @PostMapping("/profile-image")
    public ApiResponse<ImageIdResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("profileImageFile") MultipartFile profileImageFile
    ) {
        ImageIdResponse response = myPageService.updateProfileImage(user, profileImageFile);
        return ApiResponse.success(response);
    }

    @GetMapping("/profile-image/{imageId}")
    public ResponseEntity<Resource> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("imageId") Long imageId
    ) {
        try {
            Resource resource = myPageService.getProfileImage(user, imageId);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            log.error("IO error : ", e);
        }

        return ResponseEntity.notFound().build();
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
