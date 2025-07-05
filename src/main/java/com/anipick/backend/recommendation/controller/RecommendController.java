package com.anipick.backend.recommendation.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.recommendation.dto.UserMainRecommendationPageDto;
import com.anipick.backend.recommendation.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendationService;

    @GetMapping("/animes")
    public ApiResponse<UserMainRecommendationPageDto> getRecommendationAnimes(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastValue", required = false) Long lastValue,
            @RequestParam(value = "size", defaultValue = "18") Long size
    ) {
        Long userId = user.getUserId();
        UserMainRecommendationPageDto result = recommendationService.getRecommendations(userId, lastId, lastValue, size);
        return ApiResponse.success(result);
    }
}
