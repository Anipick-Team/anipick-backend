package com.anipick.backend.recommendation.controller;

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
}
