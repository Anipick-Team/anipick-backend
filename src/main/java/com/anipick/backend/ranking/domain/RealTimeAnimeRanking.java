package com.anipick.backend.ranking.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealTimeAnimeRanking {
    private Long realTimeAnimeRankingId;
    private Long animeId;
    private Long rankNum;
    private LocalDateTime createdAt;
}
