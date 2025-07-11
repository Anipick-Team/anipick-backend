package com.anipick.backend.ranking.dto;

import lombok.Getter;

@Getter
public class RedisRealTimeRankingAnimesDto {
    private Long rank;
    private Long change;
    private String trend;
    private Long animeId;
}
