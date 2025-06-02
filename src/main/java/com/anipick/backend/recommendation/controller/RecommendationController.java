package com.anipick.backend.recommendation.controller;

import com.anipick.backend.recommendation.service.RecommendationService2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/animes")
	public ApiResponse<UserMainRecommendationPageDto> getRecommendationAnimes(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(value = "lastValue", required = false) Long lastScore,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		Long userId = user.getUserId();
		UserMainRecommendationPageDto result = recommendationService.getRecommendationAnimes(userId, lastScore, lastId, size);
		return ApiResponse.success(result);
	}
	// TODO : 각 컨트롤러에 맞는 메서드 분리하기
	@GetMapping("/animes/test1")
	public ApiResponse<UserMainRecommendationPageDto> test1_only_count(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		Long userId = user.getUserId();
		UserMainRecommendationPageDto result =
			recommendationService2.test1(lastValue, lastCount, lastId, size, userId);
		return ApiResponse.success(result);
	}

	@GetMapping("/animes/test2")
	public ApiResponse<UserMainRecommendationPageDto> test2_only_score(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		Long userId = user.getUserId();
		UserMainRecommendationPageDto result =
			recommendationService2.test2(lastValue, lastCount, lastId, size, userId);
		return ApiResponse.success(result);
	}

	@GetMapping("/animes/test3")
	public ApiResponse<UserMainRecommendationPageDto> test3_countDescAndScoreDesc(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		Long userId = user.getUserId();
		UserMainRecommendationPageDto result =
			recommendationService2.test3(lastValue, lastCount, lastId, size, userId);
		return ApiResponse.success(result);
	}

	@GetMapping("/animes/test4")
	public ApiResponse<UserMainRecommendationPageDto> test4_scoreDescAndCountDesc(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam(value = "lastValue", required = false) Long lastValue,
		@RequestParam(value = "lastCount", required = false) Long lastCount,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size
	) {
		Long userId = user.getUserId();
		UserMainRecommendationPageDto result =
			recommendationService2.test4(lastValue, lastCount, lastId, size, userId);
		return ApiResponse.success(result);
	}
}
