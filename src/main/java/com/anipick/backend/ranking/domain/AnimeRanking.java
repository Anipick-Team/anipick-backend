package com.anipick.backend.ranking.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnimeRanking {
    private Long animeRankingId;
    private Long animeId;
    private Long genreId;
    private Integer year;
    private Integer seasonInt;
    private String rankType;
    private Long rankNum;
    private LocalDateTime rankDate;
    private Boolean isAllTime;
    private String context;
}
