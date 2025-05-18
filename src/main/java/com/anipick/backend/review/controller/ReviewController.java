package com.anipick.backend.review.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.review.dto.RecentReviewPageDto;
import com.anipick.backend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/recent")
    public ApiResponse<RecentReviewPageDto> getRecentReviews(
        @RequestParam(value = "lastId", required = false) Long lastId,
        @RequestParam(value = "size", defaultValue = "20") int size,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        RecentReviewPageDto page = reviewService.getRecentReviews(userId, lastId, size);
        return ApiResponse.success(page);
    }
}
