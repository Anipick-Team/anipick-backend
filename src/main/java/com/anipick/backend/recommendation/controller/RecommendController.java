package com.anipick.backend.recommendation.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.recommendation.dto.UserLastDetailAnimeRecommendationPageDto;
import com.anipick.backend.recommendation.dto.UserMainRecommendationPageDto;
import com.anipick.backend.recommendation.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendationService;

    @GetMapping("/animes")
    public ApiResponse<UserMainRecommendationPageDto> getRecommendationAnimes(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastValue", required = false) String lastValue,
            @RequestParam(value = "size", defaultValue = "18") Long size
    ) {
        Long userId = user.getUserId();
        boolean lastValueIsText = StringUtils.hasText(lastValue);
        Long lastValueToLong;
        if (lastValueIsText) {
            lastValueToLong = Long.valueOf(lastValue);
        } else {
            lastValueToLong = null;
        }
        UserMainRecommendationPageDto result = recommendationService.getRecommendations(userId, lastId, lastValueToLong, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/animes/{animeId}/recent")
    public ApiResponse<UserLastDetailAnimeRecommendationPageDto> getRecommendationLastDetailAnimes(
            @PathVariable(value = "animeId") Long animeId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastValue", required = false) String lastValue,
            @RequestParam(value = "size", defaultValue = "18") Long size
    ) {
        Long userId = user.getUserId();
        boolean lastValueIsText = StringUtils.hasText(lastValue);
        Long lastValueToLong;
        if (lastValueIsText) {
            lastValueToLong = Long.valueOf(lastValue);
        } else {
            lastValueToLong = null;
        }
        UserLastDetailAnimeRecommendationPageDto result =
                recommendationService.getLastDetailAnimeRecommendations(animeId, userId, lastId, lastValueToLong, size);
        return ApiResponse.success(result);
    }
}
