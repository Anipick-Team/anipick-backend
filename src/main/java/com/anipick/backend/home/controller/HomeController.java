package com.anipick.backend.home.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.home.dto.HomeComingSoonItemDto;
import com.anipick.backend.home.dto.HomeRecentReviewItemDto;
import com.anipick.backend.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/reviews/recent")
    public ApiResponse<List<HomeRecentReviewItemDto>> getHomeRecentReviews(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        List<HomeRecentReviewItemDto> result = homeService.getRecentReviews(userId);
        return ApiResponse.success(result);
    }

    @GetMapping("/animes/coming-soon")
    public ApiResponse<List<HomeComingSoonItemDto>>  getHomeComingSoonAnimes(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        List<HomeComingSoonItemDto> result = homeService.getComingSoonAnimes();
        return ApiResponse.success(result);
    }
}
