package com.anipick.backend.test;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.service.RatingService;
import com.anipick.backend.search.dto.SearchAnimePageDto;
import com.anipick.backend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final RatingService ratingService;
    private final TestService testService;

    //TODO : 평가 작성
    @PostMapping("/{animeId}/animes/{userId}")
    public ApiResponse<Void> createReviewRating(
        @PathVariable(name = "animeId") Long animeId,
        @RequestBody ReviewRatingRequest request,
        @PathVariable(value = "userId") long userId
    ) {
        ratingService.createReviewRating(animeId, request, userId);
        return ApiResponse.success();
    }
    //TODO : 평가 삭제
    @DeleteMapping("/{reviewId}/animes/{userId}")
    public ApiResponse<Void> deleteReviewRating(
        @PathVariable(name = "reviewId") Long reviewId,
        @RequestBody ReviewRatingRequest request,
        @PathVariable(value = "userId") long userId
    ) {
        ratingService.deleteReviewRating(reviewId, request, userId);
        return ApiResponse.success();
    }
    //TODO : 내가 쓴 평가 조회 -> 리뷰 ID, 애니 이름(영어), 애니 번호
    @GetMapping("/{userId}")
    public ApiResponse<List<TestResponseDto>> findReview(
        @PathVariable(value = "userId") long userId
    ) {
        List<TestResponseDto> result = testService.findReviews(userId);
        return ApiResponse.success(result);
    }
    //TODO : 애니 검색
    @GetMapping("/animes")
	public ApiResponse<SearchAnimePageDto> findSearchAnimes(
		@RequestParam(value = "query", required = false) String query,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		if (query == null || query.isBlank()) {
			throw new CustomException(ErrorCode.EMPTY_KEYWORD);
		}
		SearchAnimePageDto searchAnimes = testService.findSearchAnimes(query, lastId, size);
		return ApiResponse.success(searchAnimes);
	}

    @GetMapping("/recommendation-state/{userId}")
    public ApiResponse<TestUserRecommendationStateDto> findRecommendationState(
            @PathVariable(value = "userId") long userId
    ) {
        TestUserRecommendationStateDto userState = testService.findRecommendationState(userId);
        return ApiResponse.success(userState);
    }
}