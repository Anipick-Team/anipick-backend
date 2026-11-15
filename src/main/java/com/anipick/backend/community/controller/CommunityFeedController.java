package com.anipick.backend.community.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.FeedPageDto;
import com.anipick.backend.community.service.CommunityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/feed")
@RequiredArgsConstructor
public class CommunityFeedController {

    private final CommunityFeedService communityFeedService;

    @GetMapping("/all")
    public ApiResponse<FeedPageDto> getAllFeed(
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        FeedPageDto result = communityFeedService.getAllFeed(lastId, size, user.getUserId());
        return ApiResponse.success(result);
    }

    @GetMapping("/watching")
    public ApiResponse<FeedPageDto> getWatchingFeed(
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        FeedPageDto result = communityFeedService.getWatchingFeed(lastId, size, user.getUserId());
        return ApiResponse.success(result);
    }

    @GetMapping("/popular")
    public ApiResponse<FeedPageDto> getPopularFeed(
            @RequestParam(value = "lastValue", required = false) String lastValue,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        FeedPageDto result = communityFeedService.getPopularFeed(lastValue, lastId, size, user.getUserId());
        return ApiResponse.success(result);
    }
}
