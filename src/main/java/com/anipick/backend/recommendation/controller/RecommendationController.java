package com.anipick.backend.recommendation.controller;

import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.service.RecommendationService2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.recommendation.service.RecommendationService;
import com.anipick.backend.recommendation.dto.UserMainRecommendationPageDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final RecommendationService2 recommendationService2;

	// 원래 개발했던 추천 방안
	@GetMapping("/animes/{userId}")
	public ApiResponse<UserMainRecommendationPageDto> getRecommendationAnimes(
//		@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable(value = "userId") long userId,
			@RequestParam(value = "lastValue", required = false) Long lastScore,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "size", defaultValue = "18") Long size,
			@RequestParam(value = "mode", defaultValue = "RECENT_HIGH")UserRecommendMode mode,
			@RequestParam(value = "topN", defaultValue="20", required = false) int topN,
    		@RequestParam(value = "topPercent", defaultValue="20", required = false) double topPercent
			) {
		UserMainRecommendationPageDto result = recommendationService
				.getRecommendationAnimes(userId, lastScore, lastId, size, mode, topN, topPercent);
		return ApiResponse.success(result);
	}
	// 카운트만 했을 경우
	@GetMapping("/animes/test1/{userId}")
	public ApiResponse<UserMainRecommendationPageDto> test1_only_count(
		@PathVariable(value = "userId") long userId,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@RequestParam(value = "mode", defaultValue = "RECENT_HIGH") UserRecommendMode mode,
	    @RequestParam(value = "topN", defaultValue = "20", required = false) int topN,
    	@RequestParam(value = "topPercent", defaultValue = "20", required = false) double topPercent
	) {
		UserMainRecommendationPageDto result =
			recommendationService2.test1(lastValue, lastCount, lastId, size, userId, mode, topN, topPercent);
		return ApiResponse.success(result);
	}

	// 스코어만 했을 경우
	@GetMapping("/animes/test2/{userId}")
	public ApiResponse<UserMainRecommendationPageDto> test2_only_score(
		@PathVariable(value = "userId") long userId,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@RequestParam(value = "mode", defaultValue = "RECENT_HIGH") UserRecommendMode mode,
	    @RequestParam(value = "topN", defaultValue = "20", required = false) int topN,
    	@RequestParam(value = "topPercent", defaultValue = "20", required = false) double topPercent
	) {
		UserMainRecommendationPageDto result =
			recommendationService2.test2(lastValue, lastCount, lastId, size, userId, mode, topN, topPercent);
		return ApiResponse.success(result);
	}

	// 카운트 + 스코어 (카운트 먼저 정렬)
	@GetMapping("/animes/test3/{userId}")
	public ApiResponse<UserMainRecommendationPageDto> test3_countDescAndScoreDesc(
		@PathVariable(value = "userId") long userId,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@RequestParam(value = "mode", defaultValue = "RECENT_HIGH") UserRecommendMode mode,
	    @RequestParam(value = "topN", defaultValue = "20", required = false) int topN,
    	@RequestParam(value = "topPercent", defaultValue = "20", required = false) double topPercent
	) {
		UserMainRecommendationPageDto result =
			recommendationService2.test3(lastValue, lastCount, lastId, size, userId, mode, topN, topPercent);
		return ApiResponse.success(result);
	}


	// 카운트 + 스코어 (스코어 먼저 정렬)
	@GetMapping("/animes/test4/{userId}")
	public ApiResponse<UserMainRecommendationPageDto> test4_scoreDescAndCountDesc(
		@PathVariable(value = "userId") long userId,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@RequestParam(value = "mode", defaultValue = "RECENT_HIGH") UserRecommendMode mode,
	    @RequestParam(value = "topN", defaultValue = "20", required = false) int topN,
    	@RequestParam(value = "topPercent", defaultValue = "20", required = false) double topPercent
	) {
		UserMainRecommendationPageDto result =
			recommendationService2.test4(lastValue, lastCount, lastId, size, userId, mode, topN, topPercent);
		return ApiResponse.success(result);
	}
}
