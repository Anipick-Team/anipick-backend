package com.anipick.backend.ranking.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingDefaults {
    public static final String RANKING_ALIAS_KEY = "rank:genre:";
    public static final String RANKING_GENRE_ALL_KEY = "rank:genre:all";
    public static final String SORT = "trending, popularity";
    public static final String COLON = ":";
    public static final String CURRENT = "current";
}