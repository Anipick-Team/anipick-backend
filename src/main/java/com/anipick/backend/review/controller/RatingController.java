package com.anipick.backend.review.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{animeId}/animes")
    public ApiResponse<Void> createReviewRating(
        @PathVariable(name = "animeId") Long animeId,
        @RequestBody ReviewRatingRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        ratingService.createReviewRating(animeId, request, userId);
        return ApiResponse.success();
    }

    @PatchMapping("/{reviewId}/animes")
    public ApiResponse<Void> updateReviewRating(
        @PathVariable(name = "reviewId") Long reviewId,
        @RequestBody ReviewRatingRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        ratingService.updateReviewRating(reviewId, request, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{reviewId}/animes")
    public ApiResponse<Void> deleteReviewRating(
        @PathVariable(name = "reviewId") Long reviewId,
        @RequestBody ReviewRatingRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        ratingService.deleteReviewRating(reviewId, userId);
        return ApiResponse.success();
    }
}
