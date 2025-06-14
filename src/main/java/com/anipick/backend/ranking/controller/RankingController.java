package com.anipick.backend.ranking.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.ranking.dto.RankingResponse;
import com.anipick.backend.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {
    private final RankingService rankingService;

    @GetMapping("/real-time")
    public ApiResponse<RankingResponse> getRealTimeRanking(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        RankingResponse response = rankingService.getRealTimeRanking(genre, lastId, size, userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{year}/{season}")
    public ApiResponse<RankingResponse> getYearSeasonRanking(
            @PathVariable("year") Integer year,
            @PathVariable("season") Integer season,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        RankingResponse response = rankingService.getYearSeasonRanking(year, season, genre, lastId, size, userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/all-time")
    public ApiResponse<RankingResponse> getAllTimeRanking(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        RankingResponse response = rankingService.getAllTimeRanking(genre, lastId, size, userId);
        return ApiResponse.success(response);
    }
}
