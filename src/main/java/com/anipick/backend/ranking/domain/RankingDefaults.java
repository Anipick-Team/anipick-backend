package com.anipick.backend.ranking.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingDefaults {
    public static final String RANKING_SNAPSHOT_KEY = "anime:ranking:snapshot:%d:%d:%s";
    public static final int MIN_YEAR = 1940;
    public static final int MAX_YEAR = 2026;
    public static final int ONE_DAY = 1;
}
