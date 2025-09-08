package com.anipick.backend.ranking.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.ranking.dto.RankingResponse;
import com.anipick.backend.ranking.dto.RealTimeRankingResponse;
import com.anipick.backend.ranking.service.RankingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {
    private final RankingService rankingService;

    @GetMapping("/real-time")
    public ApiResponse<RealTimeRankingResponse> getRealTimeRanking(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastValue", required = false) Long lastValue,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size
    ) {
        RealTimeRankingResponse response = rankingService.getRealTimeRanking(genre, lastId, lastValue, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/year-season")
    public ApiResponse<RankingResponse> getYearSeasonRanking(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "season", required = false) Integer season,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastRank", defaultValue = "0", required = false) Long lastRank,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size
    ) throws JsonProcessingException {
        RankingResponse response = rankingService.getYearSeasonRanking(year, season, genre, lastId, lastRank, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/all-time")
    public ApiResponse<RankingResponse> getAllTimeRanking(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastRank", defaultValue = "0", required = false) Long lastRank,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size
    ) {
        RankingResponse response = rankingService.getAllTimeRanking(genre, lastId, lastRank, size);
        return ApiResponse.success(response);
    }
}
