package com.anipick.backend.review.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.review.dto.RecentReviewPageDto;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.dto.ReviewRequest;
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
        @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Long userId = 1L; // TODO: JWT 연동 후 실제 userId 로 교체
        RecentReviewPageDto page = reviewService.getRecentReviews(userId, lastId, size);
        return ApiResponse.success(page);
    }


    @PatchMapping("/{reviewId}/animes")
    public ApiResponse<Void> createAndUpdateReview(
        @PathVariable(name = "reviewId") Long reviewId,
        @RequestBody ReviewRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        reviewService.createAndUpdateReview(reviewId, request, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
        @PathVariable(name = "reviewId") Long reviewId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.success();
    }
}
