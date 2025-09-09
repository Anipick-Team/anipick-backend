package com.anipick.backend.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedisRealTimeRankingAnimesDto {
    private Long animeId;

    public static RedisRealTimeRankingAnimesDto of(Long animeId) {
        return new RedisRealTimeRankingAnimesDto(animeId);
    }
}
